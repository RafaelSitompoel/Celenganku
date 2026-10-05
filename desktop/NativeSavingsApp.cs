using System;
using System.Collections.Generic;
using System.Diagnostics;
using System.Drawing;
using System.Globalization;
using System.IO;
using System.IO.Compression;
using System.Linq;
using System.Text;
using System.Net;
using System.Threading;
using System.Web.Script.Serialization;
using System.Windows.Forms;

namespace CelengankuNative
{
    internal static class Program
    {
        [STAThread]
        private static void Main()
        {
            Application.EnableVisualStyles();
            Application.SetCompatibleTextRenderingDefault(false);
            while (true)
            {
                if (!InternetProbe.IsAvailable())
                {
                    DialogResult retryInternet = MessageBox.Show(
                        "Celenganku memerlukan koneksi internet. Periksa koneksi lalu pilih Coba Lagi.",
                        "Koneksi internet diperlukan",
                        MessageBoxButtons.RetryCancel,
                        MessageBoxIcon.Warning);
                    if (retryInternet != DialogResult.Retry) return;
                    continue;
                }

                MaintenanceStatus maintenance;
                if (!MaintenanceService.TryRead(out maintenance))
                {
                    MessageBox.Show(
                        "Status server maintenance tidak dapat diverifikasi. Celenganku tetap akan dibuka, tetapi status maintenance belum tersedia.",
                        "Peringatan status server",
                        MessageBoxButtons.OK,
                        MessageBoxIcon.Warning);
                    break;
                }

                if (maintenance.Enabled)
                {
                    string title = String.IsNullOrWhiteSpace(maintenance.Title) ? "Celenganku sedang maintenance" : maintenance.Title;
                    string message = String.IsNullOrWhiteSpace(maintenance.Message) ? "Silakan coba lagi nanti." : maintenance.Message;
                    DialogResult retryMaintenance = MessageBox.Show(message, title, MessageBoxButtons.RetryCancel, MessageBoxIcon.Information);
                    if (retryMaintenance != DialogResult.Retry) return;
                    continue;
                }

                break;
            }
            Application.Run(new SavingsForm());
        }
    }

    internal sealed class SavingsForm : Form
    {
        private static readonly Color Ink = Color.FromArgb(25, 39, 52);
        private static readonly Color Green = Color.FromArgb(0, 133, 97);
        private static readonly Color Canvas = Color.FromArgb(244, 247, 246);
        private readonly NativeDataStore store;
        private readonly DataGridView recentGrid;
        private readonly DataGridView transactionGrid;
        private readonly DataGridView goalGrid;
        private readonly DataGridView wishlistGrid;
        private readonly Label balanceValue;
        private readonly Label targetValue;
        private readonly Label savedValue;
        private readonly Label dataCountValue;
        private readonly Label connectionStatus;
        private readonly TabControl tabs;
        private readonly System.Windows.Forms.Timer connectivityTimer;
        private int connectionCheckInProgress;
    private bool maintenanceWasActive;

        public SavingsForm()
        {
            store = new NativeDataStore();
            Text = "Celenganku - Tabungan Pribadi";
            StartPosition = FormStartPosition.CenterScreen;
            MinimumSize = new Size(980, 680);
            Size = new Size(1180, 790);
            BackColor = Canvas;
            Font = new Font("Segoe UI", 10F);

            try
            {
                string iconPath = Path.Combine(AppDomain.CurrentDomain.BaseDirectory, "app.ico");
                if (File.Exists(iconPath)) Icon = new Icon(iconPath);
            }
            catch { }

            Panel header = new Panel { Dock = DockStyle.Top, Height = 92, BackColor = Ink };
            Label title = new Label
            {
                Text = "Celenganku",
                Font = new Font("Segoe UI", 22F, FontStyle.Bold),
                ForeColor = Color.White,
                Location = new Point(28, 15),
                AutoSize = true
            };
            Label subtitle = new Label
            {
                Text = "Kelola tabungan, transaksi, dan target keuangan",
                Font = new Font("Segoe UI", 10F),
                ForeColor = Color.FromArgb(204, 220, 216),
                Location = new Point(31, 57),
                AutoSize = true
            };
            header.Controls.Add(title);
            header.Controls.Add(subtitle);
            connectionStatus = new Label
            {
                Text = "● Online",
                Font = new Font("Segoe UI", 10F, FontStyle.Bold),
                ForeColor = Color.FromArgb(115, 220, 175),
                TextAlign = ContentAlignment.MiddleRight,
                Location = new Point(850, 34),
                Size = new Size(300, 28),
                Anchor = AnchorStyles.Top | AnchorStyles.Right
            };
            header.Controls.Add(connectionStatus);
            Controls.Add(header);

            tabs = new TabControl { Dock = DockStyle.Fill, Padding = new Point(16, 8) };
            balanceValue = new Label();
            targetValue = new Label();
            savedValue = new Label();
            dataCountValue = new Label();
            recentGrid = CreateGrid();
            transactionGrid = CreateGrid();
            goalGrid = CreateGrid();
            wishlistGrid = CreateGrid();

            tabs.TabPages.Add(BuildDashboardTab());
            tabs.TabPages.Add(BuildTransactionsTab());
            tabs.TabPages.Add(BuildGoalsTab());
            tabs.TabPages.Add(BuildWishlistTab());
            tabs.TabPages.Add(BuildDataTab());
            Controls.Add(tabs);
            tabs.BringToFront();
            header.BringToFront();

            connectivityTimer = new System.Windows.Forms.Timer { Interval = 30000 };
            connectivityTimer.Tick += delegate { CheckInternetInBackground(); };
            Shown += delegate
            {
                RefreshData();
                connectivityTimer.Start();
                CheckInternetInBackground();
            };
            FormClosed += delegate { connectivityTimer.Stop(); };
            FormClosed += delegate { store.WriteLog("Aplikasi ditutup."); };
        }

        private void CheckInternetInBackground()
        {
            if (Interlocked.Exchange(ref connectionCheckInProgress, 1) != 0) return;
            ThreadPool.QueueUserWorkItem(delegate
            {
                bool internetAvailable = InternetProbe.IsAvailable();
                MaintenanceStatus maintenance = null;
                bool statusAvailable = internetAvailable && MaintenanceService.TryRead(out maintenance);
                bool accessAllowed = internetAvailable && (!statusAvailable || maintenance == null || !maintenance.Enabled);
                if (IsDisposed || !IsHandleCreated) return;
                try
                {
                    BeginInvoke((MethodInvoker)delegate
                    {
                        connectionCheckInProgress = 0;
                        tabs.Enabled = accessAllowed;
                        if (!internetAvailable)
                        {
                            connectionStatus.Text = "● Offline - fitur dijeda";
                            connectionStatus.ForeColor = Color.FromArgb(255, 177, 163);
                        }
                        else if (!statusAvailable || maintenance == null)
                        {
                            connectionStatus.Text = "● Maintenance server tidak tersedia";
                            connectionStatus.ForeColor = Color.FromArgb(255, 210, 130);
                        }
                        else if (maintenance.Enabled)
                        {
                            connectionStatus.Text = "● Server maintenance";
                            connectionStatus.ForeColor = Color.FromArgb(255, 177, 163);
                            if (!maintenanceWasActive)
                            {
                                string title = String.IsNullOrWhiteSpace(maintenance.Title) ? "Celenganku sedang maintenance" : maintenance.Title;
                                string message = String.IsNullOrWhiteSpace(maintenance.Message) ? "Silakan coba lagi nanti." : maintenance.Message;
                                MessageBox.Show(this, message, title, MessageBoxButtons.OK, MessageBoxIcon.Information);
                            }
                        }
                        else
                        {
                            connectionStatus.Text = "● Online";
                            connectionStatus.ForeColor = Color.FromArgb(115, 220, 175);
                        }
                        maintenanceWasActive = statusAvailable && maintenance != null && maintenance.Enabled;
                    });
                }
                catch { connectionCheckInProgress = 0; }
            });
        }

        private TabPage BuildDashboardTab()
        {
            TabPage page = CreatePage("Ringkasan");
            FlowLayoutPanel metrics = new FlowLayoutPanel
            {
                Dock = DockStyle.Top,
                Height = 142,
                Padding = new Padding(18, 18, 8, 8),
                WrapContents = false
            };
            metrics.Controls.Add(CreateMetric("Saldo saat ini", balanceValue));
            metrics.Controls.Add(CreateMetric("Total target", targetValue));
            metrics.Controls.Add(CreateMetric("Terkumpul untuk target", savedValue));

            FlowLayoutPanel actions = new FlowLayoutPanel
            {
                Dock = DockStyle.Top,
                Height = 66,
                Padding = new Padding(20, 8, 8, 8),
                WrapContents = false
            };
            actions.Controls.Add(CreateButton("+ Setor", Green, delegate { AddTransaction(true); }));
            actions.Controls.Add(CreateButton("- Tarik", Color.FromArgb(183, 73, 62), delegate { AddTransaction(false); }));

            Label recentTitle = new Label
            {
                Text = "Transaksi terbaru",
                Dock = DockStyle.Top,
                Height = 38,
                Padding = new Padding(22, 8, 0, 0),
                Font = new Font("Segoe UI", 12F, FontStyle.Bold),
                ForeColor = Ink
            };
            recentGrid.Dock = DockStyle.Fill;
            page.Controls.Add(recentGrid);
            page.Controls.Add(recentTitle);
            page.Controls.Add(actions);
            page.Controls.Add(metrics);
            return page;
        }

        private TabPage BuildTransactionsTab()
        {
            TabPage page = CreatePage("Transaksi");
            transactionGrid.Dock = DockStyle.Fill;
            page.Controls.Add(transactionGrid);
            return page;
        }

        private TabPage BuildGoalsTab()
        {
            TabPage page = CreatePage("Target");
            FlowLayoutPanel actions = CreateActionBar();
            actions.Controls.Add(CreateButton("Tambah target", Green, delegate { AddGoal(); }));
            actions.Controls.Add(CreateButton("Hapus target terpilih", Color.FromArgb(183, 73, 62), delegate { DeleteGoal(); }));
            goalGrid.Dock = DockStyle.Fill;
            page.Controls.Add(goalGrid);
            page.Controls.Add(actions);
            return page;
        }

        private TabPage BuildWishlistTab()
        {
            TabPage page = CreatePage("Daftar impian");
            FlowLayoutPanel actions = CreateActionBar();
            actions.Controls.Add(CreateButton("Tambah impian", Green, delegate { AddWishlistItem(); }));
            actions.Controls.Add(CreateButton("Hapus pilihan", Color.FromArgb(183, 73, 62), delegate { DeleteWishlistItem(); }));
            wishlistGrid.Dock = DockStyle.Fill;
            page.Controls.Add(wishlistGrid);
            page.Controls.Add(actions);
            return page;
        }

        private TabPage BuildDataTab()
        {
            TabPage page = CreatePage("Data & backup");
            Panel content = new Panel { Dock = DockStyle.Top, Padding = new Padding(24), Height = 250 };
            Label heading = new Label
            {
                Text = "Penyimpanan lokal",
                Font = new Font("Segoe UI", 16F, FontStyle.Bold),
                ForeColor = Ink,
                Location = new Point(24, 22),
                AutoSize = true
            };
            Label folder = new Label
            {
                Text = store.DataDirectory,
                ForeColor = Color.FromArgb(81, 99, 109),
                Location = new Point(27, 62),
                AutoSize = true
            };
            dataCountValue.Location = new Point(27, 94);
            dataCountValue.AutoSize = true;
            dataCountValue.ForeColor = Ink;
            Button open = CreateButton("Buka folder data", Ink, delegate { store.OpenDataFolder(); });
            open.Location = new Point(27, 130);
            Button backup = CreateButton("Buat backup sekarang", Green, delegate { CreateBackup(); });
            backup.Location = new Point(205, 130);
            content.Controls.Add(heading);
            content.Controls.Add(folder);
            content.Controls.Add(dataCountValue);
            content.Controls.Add(open);
            content.Controls.Add(backup);
            page.Controls.Add(content);
            return page;
        }

        private void AddTransaction(bool deposit)
        {
            using (RecordDialog dialog = new RecordDialog(deposit ? "Setor tabungan" : "Tarik tabungan", "Keterangan", "Nominal (Rp)"))
            {
                if (dialog.ShowDialog(this) != DialogResult.OK) return;
                long amount;
                if (!TryParseAmount(dialog.AmountText, out amount) || amount <= 0)
                {
                    MessageBox.Show("Masukkan nominal yang lebih besar dari 0.", "Nominal tidak valid", MessageBoxButtons.OK, MessageBoxIcon.Warning);
                    return;
                }
                if (!deposit && amount > store.Savings.Balance)
                {
                    MessageBox.Show("Saldo tidak mencukupi untuk penarikan ini.", "Saldo tidak cukup", MessageBoxButtons.OK, MessageBoxIcon.Warning);
                    return;
                }

                store.Transactions.Add(new TransactionData
                {
                    Date = DateTime.Now,
                    Type = deposit ? "Setor" : "Tarik",
                    Description = String.IsNullOrWhiteSpace(dialog.NameText) ? (deposit ? "Setoran" : "Penarikan") : dialog.NameText.Trim(),
                    Amount = amount
                });
                store.Savings.Balance += deposit ? amount : -amount;
                store.SaveTransactions();
                store.SaveSavings();
                store.WriteLog((deposit ? "Setor " : "Tarik ") + amount.ToString(CultureInfo.InvariantCulture));
                RefreshData();
            }
        }

        private void AddGoal()
        {
            using (RecordDialog dialog = new RecordDialog("Target tabungan baru", "Nama target", "Nominal target (Rp)"))
            {
                if (dialog.ShowDialog(this) != DialogResult.OK) return;
                long amount;
                if (String.IsNullOrWhiteSpace(dialog.NameText) || !TryParseAmount(dialog.AmountText, out amount) || amount <= 0)
                {
                    MessageBox.Show("Isi nama target dan nominal yang lebih besar dari 0.", "Data belum lengkap", MessageBoxButtons.OK, MessageBoxIcon.Warning);
                    return;
                }
                store.Goals.Add(new GoalData { Name = dialog.NameText.Trim(), Target = amount, Saved = 0, Created = DateTime.Now });
                store.SaveGoals();
                store.WriteLog("Target ditambahkan: " + dialog.NameText.Trim());
                RefreshData();
            }
        }

        private void AddWishlistItem()
        {
            using (RecordDialog dialog = new RecordDialog("Impian baru", "Nama impian", "Estimasi biaya (Rp)"))
            {
                if (dialog.ShowDialog(this) != DialogResult.OK) return;
                long amount;
                if (String.IsNullOrWhiteSpace(dialog.NameText) || !TryParseAmount(dialog.AmountText, out amount) || amount <= 0)
                {
                    MessageBox.Show("Isi nama impian dan estimasi biaya yang lebih besar dari 0.", "Data belum lengkap", MessageBoxButtons.OK, MessageBoxIcon.Warning);
                    return;
                }
                store.Wishlist.Add(new WishlistData { Name = dialog.NameText.Trim(), EstimatedCost = amount, Created = DateTime.Now });
                store.SaveWishlist();
                RefreshData();
            }
        }

        private void DeleteGoal()
        {
            if (goalGrid.CurrentRow == null || goalGrid.CurrentRow.Index >= store.Goals.Count) return;
            store.Goals.RemoveAt(goalGrid.CurrentRow.Index);
            store.SaveGoals();
            RefreshData();
        }

        private void DeleteWishlistItem()
        {
            if (wishlistGrid.CurrentRow == null || wishlistGrid.CurrentRow.Index >= store.Wishlist.Count) return;
            store.Wishlist.RemoveAt(wishlistGrid.CurrentRow.Index);
            store.SaveWishlist();
            RefreshData();
        }

        private void CreateBackup()
        {
            try
            {
                string path = store.CreateBackup();
                MessageBox.Show("Backup berhasil dibuat:\n" + path, "Backup selesai", MessageBoxButtons.OK, MessageBoxIcon.Information);
                RefreshData();
            }
            catch (Exception ex)
            {
                MessageBox.Show("Backup gagal: " + ex.Message, "Backup gagal", MessageBoxButtons.OK, MessageBoxIcon.Error);
            }
        }

        private void RefreshData()
        {
            balanceValue.Text = FormatMoney(store.Savings.Balance);
            targetValue.Text = FormatMoney(store.Goals.Sum(delegate(GoalData goal) { return goal.Target; }));
            savedValue.Text = FormatMoney(store.Goals.Sum(delegate(GoalData goal) { return goal.Saved; }));
            FillTransactions(recentGrid, store.Transactions.OrderByDescending(delegate(TransactionData item) { return item.Date; }).Take(8));
            FillTransactions(transactionGrid, store.Transactions.OrderByDescending(delegate(TransactionData item) { return item.Date; }));
            goalGrid.Columns.Clear();
            AddTextColumn(goalGrid, "Nama target", "Name", 1.5F);
            AddTextColumn(goalGrid, "Target", "TargetText", 1F);
            AddTextColumn(goalGrid, "Terkumpul", "SavedText", 1F);
            goalGrid.Rows.Clear();
            foreach (GoalData goal in store.Goals) goalGrid.Rows.Add(goal.Name, FormatMoney(goal.Target), FormatMoney(goal.Saved));
            wishlistGrid.Columns.Clear();
            AddTextColumn(wishlistGrid, "Nama impian", "Name", 1.5F);
            AddTextColumn(wishlistGrid, "Estimasi biaya", "Cost", 1F);
            wishlistGrid.Rows.Clear();
            foreach (WishlistData item in store.Wishlist) wishlistGrid.Rows.Add(item.Name, FormatMoney(item.EstimatedCost));
            dataCountValue.Text = store.JsonFileCount.ToString(CultureInfo.InvariantCulture) + " file data JSON aktif, tersimpan di luar folder instalasi.";
        }

        private static void FillTransactions(DataGridView grid, IEnumerable<TransactionData> items)
        {
            grid.Columns.Clear();
            AddTextColumn(grid, "Tanggal", "Date", 1F);
            AddTextColumn(grid, "Jenis", "Type", 0.7F);
            AddTextColumn(grid, "Keterangan", "Description", 1.5F);
            AddTextColumn(grid, "Nominal", "Amount", 1F);
            grid.Rows.Clear();
            foreach (TransactionData item in items)
                grid.Rows.Add(item.Date.ToString("dd MMM yyyy  HH:mm", CultureInfo.GetCultureInfo("id-ID")), item.Type, item.Description, FormatMoney(item.Amount));
        }

        private static DataGridView CreateGrid()
        {
            return new DataGridView
            {
                ReadOnly = true,
                AllowUserToAddRows = false,
                AllowUserToDeleteRows = false,
                AllowUserToResizeRows = false,
                AutoSizeColumnsMode = DataGridViewAutoSizeColumnsMode.Fill,
                BackgroundColor = Color.White,
                BorderStyle = BorderStyle.None,
                CellBorderStyle = DataGridViewCellBorderStyle.SingleHorizontal,
                ColumnHeadersHeight = 38,
                Dock = DockStyle.Fill,
                EnableHeadersVisualStyles = false,
                RowHeadersVisible = false,
                SelectionMode = DataGridViewSelectionMode.FullRowSelect,
                ColumnHeadersDefaultCellStyle = new DataGridViewCellStyle { BackColor = Ink, ForeColor = Color.White, Font = new Font("Segoe UI", 9F, FontStyle.Bold) },
                DefaultCellStyle = new DataGridViewCellStyle { BackColor = Color.White, ForeColor = Ink, SelectionBackColor = Color.FromArgb(215, 239, 231), SelectionForeColor = Ink, Padding = new Padding(6) }
            };
        }

        private static void AddTextColumn(DataGridView grid, string title, string name, float fill)
        {
            grid.Columns.Add(new DataGridViewTextBoxColumn { HeaderText = title, Name = name, FillWeight = fill, SortMode = DataGridViewColumnSortMode.NotSortable });
        }

        private static TabPage CreatePage(string title)
        {
            return new TabPage(title) { BackColor = Canvas, Padding = new Padding(8) };
        }

        private static Panel CreateMetric(string caption, Label value)
        {
            Panel panel = new Panel { Width = 270, Height = 104, BackColor = Color.White, Margin = new Padding(0, 0, 14, 0) };
            Label label = new Label { Text = caption, ForeColor = Color.FromArgb(89, 105, 113), Location = new Point(16, 14), AutoSize = true };
            value.Text = "Rp 0";
            value.Font = new Font("Segoe UI", 17F, FontStyle.Bold);
            value.ForeColor = Ink;
            value.Location = new Point(16, 44);
            value.AutoSize = true;
            panel.Controls.Add(label);
            panel.Controls.Add(value);
            return panel;
        }

        private static FlowLayoutPanel CreateActionBar()
        {
            return new FlowLayoutPanel { Dock = DockStyle.Top, Height = 64, Padding = new Padding(14, 8, 8, 8), WrapContents = false };
        }

        private static Button CreateButton(string text, Color color, EventHandler handler)
        {
            Button button = new Button
            {
                Text = text,
                BackColor = color,
                ForeColor = Color.White,
                FlatStyle = FlatStyle.Flat,
                Font = new Font("Segoe UI", 9.5F, FontStyle.Bold),
                Height = 40,
                Width = 165,
                Margin = new Padding(0, 0, 10, 0),
                Cursor = Cursors.Hand
            };
            button.FlatAppearance.BorderSize = 0;
            button.Click += handler;
            return button;
        }

        private static bool TryParseAmount(string text, out long amount)
        {
            decimal value;
            if (Decimal.TryParse(text, NumberStyles.Number, CultureInfo.GetCultureInfo("id-ID"), out value) && value > 0 && value <= Int64.MaxValue)
            {
                amount = Decimal.ToInt64(value);
                return true;
            }
            amount = 0;
            return false;
        }

        private static string FormatMoney(long value)
        {
            return String.Format(CultureInfo.GetCultureInfo("id-ID"), "Rp {0:N0}", value);
        }
    }

    internal sealed class RecordDialog : Form
    {
        private readonly TextBox nameBox;
        private readonly TextBox amountBox;
        public string NameText { get { return nameBox.Text; } }
        public string AmountText { get { return amountBox.Text; } }

        public RecordDialog(string title, string nameLabel, string amountLabel)
        {
            Text = title;
            StartPosition = FormStartPosition.CenterParent;
            FormBorderStyle = FormBorderStyle.FixedDialog;
            MinimizeBox = false;
            MaximizeBox = false;
            ShowInTaskbar = false;
            ClientSize = new Size(410, 220);
            Font = new Font("Segoe UI", 10F);
            BackColor = Color.FromArgb(244, 247, 246);

            Label firstLabel = new Label { Text = nameLabel, Location = new Point(22, 20), AutoSize = true };
            nameBox = new TextBox { Location = new Point(25, 46), Width = 355, Height = 28 };
            Label secondLabel = new Label { Text = amountLabel, Location = new Point(22, 87), AutoSize = true };
            amountBox = new TextBox { Location = new Point(25, 113), Width = 355, Height = 28 };
            Button save = new Button { Text = "Simpan", DialogResult = DialogResult.OK, Location = new Point(215, 165), Size = new Size(78, 36), BackColor = Color.FromArgb(0, 133, 97), ForeColor = Color.White, FlatStyle = FlatStyle.Flat };
            Button cancel = new Button { Text = "Batal", DialogResult = DialogResult.Cancel, Location = new Point(302, 165), Size = new Size(78, 36) };
            save.FlatAppearance.BorderSize = 0;
            AcceptButton = save;
            CancelButton = cancel;
            Controls.Add(firstLabel);
            Controls.Add(nameBox);
            Controls.Add(secondLabel);
            Controls.Add(amountBox);
            Controls.Add(save);
            Controls.Add(cancel);
        }
    }

    internal sealed class NativeDataStore
    {
        private readonly JavaScriptSerializer serializer = new JavaScriptSerializer();
        public readonly string DataDirectory;
        public SavingsData Savings;
        public List<TransactionData> Transactions;
        public List<GoalData> Goals;
        public List<WishlistData> Wishlist;
        public int JsonFileCount { get { return Directory.GetFiles(DataDirectory, "*.json", SearchOption.AllDirectories).Length; } }

        public NativeDataStore()
        {
            DataDirectory = Path.Combine(Environment.GetFolderPath(Environment.SpecialFolder.LocalApplicationData), "Celenganku", "Data");
            EnsureStructure();
            Savings = Load("savings.json", new SavingsData());
            Transactions = Load("transactions.json", new List<TransactionData>());
            Goals = Load("goals.json", new List<GoalData>());
            Wishlist = Load("wishlist.json", new List<WishlistData>());
            WriteLog("Aplikasi dibuka.");
        }

        public void SaveSavings() { Save("savings.json", Savings); }
        public void SaveTransactions() { Save("transactions.json", Transactions); }
        public void SaveGoals() { Save("goals.json", Goals); }
        public void SaveWishlist() { Save("wishlist.json", Wishlist); }

        public void WriteLog(string message)
        {
            string logPath = Path.Combine(DataDirectory, "logs", "activity.log");
            File.AppendAllText(logPath, DateTime.Now.ToString("s", CultureInfo.InvariantCulture) + " " + message + Environment.NewLine, new UTF8Encoding(false));
        }

        public string CreateBackup()
        {
            string backupDirectory = Path.Combine(Path.GetDirectoryName(DataDirectory), "Backups", "manual");
            Directory.CreateDirectory(backupDirectory);
            string backup = Path.Combine(backupDirectory, "Celenganku-" + DateTime.Now.ToString("yyyyMMdd-HHmmss", CultureInfo.InvariantCulture) + ".zip");
            ZipFile.CreateFromDirectory(DataDirectory, backup, CompressionLevel.Optimal, true);
            return backup;
        }

        public void OpenDataFolder()
        {
            Process.Start(new ProcessStartInfo { FileName = DataDirectory, UseShellExecute = true });
        }

        private void EnsureStructure()
        {
            string[] folders = { "backups\\daily", "backups\\weekly", "backups\\manual", "reports\\monthly", "reports\\yearly", "logs", "cache", "config" };
            Directory.CreateDirectory(DataDirectory);
            foreach (string folder in folders) Directory.CreateDirectory(Path.Combine(DataDirectory, folder));

            EnsureJson("users.json", new[] { new UserData { Name = Environment.UserName, Created = DateTime.Now } });
            EnsureJson("settings.json", new Dictionary<string, string> { { "currency", "IDR" }, { "language", "id-ID" }, { "theme", "system" } });
            EnsureJson("categories.json", new[] { "Tabungan", "Kebutuhan", "Pendidikan", "Kesehatan", "Hiburan", "Lainnya" });
            EnsureJson("achievements.json", new List<string>());
            EnsureJson("notifications.json", new List<string>());
            EnsureJson("quick-actions.json", new[] { "Setor", "Tarik", "Target", "Backup" });
            EnsureJson("reports\\monthly\\summary.json", new Dictionary<string, long>());
            EnsureJson("reports\\yearly\\summary.json", new Dictionary<string, long>());
            EnsureJson("config\\app-config.json", new Dictionary<string, string> { { "schemaVersion", "1" } });
            EnsureJson("savings.json", new SavingsData());
            EnsureJson("transactions.json", new List<TransactionData>());
            EnsureJson("goals.json", new List<GoalData>());
            EnsureJson("wishlist.json", new List<WishlistData>());
            EnsureJson("audit-log.json", new List<string>());
        }

        private T Load<T>(string relativePath, T fallback)
        {
            string path = Path.Combine(DataDirectory, relativePath);
            try { return serializer.Deserialize<T>(File.ReadAllText(path, Encoding.UTF8)); }
            catch { return fallback; }
        }

        private void Save<T>(string relativePath, T value)
        {
            string path = Path.Combine(DataDirectory, relativePath);
            File.WriteAllText(path, serializer.Serialize(value), new UTF8Encoding(false));
        }

        private void EnsureJson<T>(string relativePath, T value)
        {
            string path = Path.Combine(DataDirectory, relativePath);
            if (!File.Exists(path)) Save(relativePath, value);
        }
    }

    public sealed class SavingsData { public long Balance { get; set; } }
        internal static class InternetProbe
        {
            public static bool IsAvailable()
            {
                try
                {
                    HttpWebRequest request = (HttpWebRequest)WebRequest.Create("http://www.msftconnecttest.com/connecttest.txt");
                    request.Method = "GET";
                    request.Timeout = 5000;
                    request.ReadWriteTimeout = 5000;
                    request.AllowAutoRedirect = true;
                    using (HttpWebResponse response = (HttpWebResponse)request.GetResponse())
                    using (Stream stream = response.GetResponseStream())
                    using (StreamReader reader = new StreamReader(stream))
                    {
                        return response.StatusCode == HttpStatusCode.OK && reader.ReadToEnd().Trim() == "Microsoft Connect Test";
                    }
                }
                catch { return false; }
            }
        }

        internal sealed class MaintenanceStatus
        {
            public bool Enabled { get; set; }
            public string Title { get; set; }
            public string Message { get; set; }
            public string ResumeAtUtc { get; set; }
        }

        internal static class MaintenanceService
        {
            private const string Endpoint = "https://raw.githubusercontent.com/RafaelSitompoel/Celenganku/main/desktop/maintenance.json";

            public static bool TryRead(out MaintenanceStatus status)
            {
                status = null;
                try
                {
                    HttpWebRequest request = (HttpWebRequest)WebRequest.Create(Endpoint + "?v=" + DateTime.UtcNow.Ticks.ToString(CultureInfo.InvariantCulture));
                    request.Method = "GET";
                    request.Accept = "application/json";
                    request.Timeout = 5000;
                    request.ReadWriteTimeout = 5000;
                    request.AllowAutoRedirect = true;
                    request.Headers[HttpRequestHeader.CacheControl] = "no-cache";

                    using (HttpWebResponse response = (HttpWebResponse)request.GetResponse())
                    using (Stream stream = response.GetResponseStream())
                    using (StreamReader reader = new StreamReader(stream))
                    {
                        if (response.StatusCode != HttpStatusCode.OK) return false;
                        string json = reader.ReadToEnd();
                        if (json.Length > 8192) return false;
                        Dictionary<string, object> values = new JavaScriptSerializer().Deserialize<Dictionary<string, object>>(json);
                        object enabled;
                        if (values == null || !values.TryGetValue("enabled", out enabled) || !(enabled is bool)) return false;

                        status = new MaintenanceStatus
                        {
                            Enabled = (bool)enabled,
                            Title = values.ContainsKey("title") ? Convert.ToString(values["title"]) : null,
                            Message = values.ContainsKey("message") ? Convert.ToString(values["message"]) : null,
                            ResumeAtUtc = values.ContainsKey("resumeAtUtc") ? Convert.ToString(values["resumeAtUtc"]) : null
                        };
                        return true;
                    }
                }
                catch { return false; }
            }
        }

        public sealed class TransactionData { public DateTime Date { get; set; } public string Type { get; set; } public string Description { get; set; } public long Amount { get; set; } }
    public sealed class GoalData { public string Name { get; set; } public long Target { get; set; } public long Saved { get; set; } public DateTime Created { get; set; } }
    public sealed class WishlistData { public string Name { get; set; } public long EstimatedCost { get; set; } public DateTime Created { get; set; } }
    public sealed class UserData { public string Name { get; set; } public DateTime Created { get; set; } }
}