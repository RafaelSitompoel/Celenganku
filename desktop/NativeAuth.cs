using System;
using System.Collections.Generic;
using System.Drawing;
using System.IO;
using System.Security.Cryptography;
using System.Text.RegularExpressions;
using System.Windows.Forms;
using System.Web.Script.Serialization;

namespace CelengankuNative
{
    internal sealed class LoginForm : Form
    {
        private static readonly Color Ink = Color.FromArgb(25, 39, 52);
        private static readonly Color Green = Color.FromArgb(0, 133, 97);
        private readonly NativeAccountStore accountStore;
        private readonly TextBox usernameBox;
        private readonly TextBox passwordBox;
        private readonly Label feedback;

        public string AuthenticatedUsername { get; private set; }

        public LoginForm()
        {
            accountStore = new NativeAccountStore();
            Text = "Masuk - Celenganku";
            StartPosition = FormStartPosition.CenterScreen;
            FormBorderStyle = FormBorderStyle.FixedDialog;
            MaximizeBox = false;
            MinimizeBox = false;
            ClientSize = new Size(900, 570);
            BackColor = Color.White;
            Font = new Font("Segoe UI", 10F);
            Icon appIcon = LoadAppIcon();
            if (appIcon != null) Icon = appIcon;

            Panel content = new Panel { Dock = DockStyle.Fill, BackColor = Color.White };
            Panel brand = new Panel { Dock = DockStyle.Left, Width = 340, BackColor = Ink };
            Controls.Add(content);
            Controls.Add(brand);

            PictureBox mascot = new PictureBox
            {
                Image = LoadMascot(),
                SizeMode = PictureBoxSizeMode.Zoom,
                Location = new Point(112, 88),
                Size = new Size(116, 116),
                BackColor = Color.Transparent
            };
            Label brandTitle = new Label
            {
                Text = "Celenganku",
                Font = new Font("Segoe UI", 24F, FontStyle.Bold),
                ForeColor = Color.White,
                TextAlign = ContentAlignment.MiddleCenter,
                Location = new Point(28, 236),
                Size = new Size(284, 44)
            };
            Label brandSubtitle = new Label
            {
                Text = "Teman kecil untuk tujuan\nkeuangan yang lebih besar.",
                Font = new Font("Segoe UI", 11F),
                ForeColor = Color.FromArgb(204, 220, 216),
                TextAlign = ContentAlignment.TopCenter,
                Location = new Point(35, 288),
                Size = new Size(270, 54)
            };
            Label brandFooter = new Label
            {
                Text = "MENABUNG SEDIKIT, BERARTI BANYAK",
                Font = new Font("Segoe UI", 8.5F, FontStyle.Bold),
                ForeColor = Color.FromArgb(115, 220, 175),
                TextAlign = ContentAlignment.MiddleCenter,
                Location = new Point(26, 496),
                Size = new Size(288, 24)
            };
            brand.Controls.Add(mascot);
            brand.Controls.Add(brandTitle);
            brand.Controls.Add(brandSubtitle);
            brand.Controls.Add(brandFooter);

            Label heading = new Label
            {
                Text = "Selamat datang kembali",
                Font = new Font("Segoe UI", 21F, FontStyle.Bold),
                ForeColor = Ink,
                Location = new Point(58, 82),
                AutoSize = true
            };
            Label description = new Label
            {
                Text = "Masuk untuk melihat perkembangan tabunganmu.",
                Font = new Font("Segoe UI", 10F),
                ForeColor = Color.FromArgb(89, 105, 113),
                Location = new Point(61, 126),
                AutoSize = true
            };
            Label usernameLabel = CreateFieldLabel("Nama pengguna", 61, 181);
            usernameBox = CreateTextBox(61, 207);
            usernameBox.MaxLength = 20;
            usernameBox.Name = "usernameBox";
            Label passwordLabel = CreateFieldLabel("Kata sandi", 61, 268);
            passwordBox = CreateTextBox(61, 294);
            passwordBox.UseSystemPasswordChar = true;
            passwordBox.Name = "passwordBox";

            CheckBox showPassword = new CheckBox
            {
                Text = "Tampilkan kata sandi",
                AutoSize = true,
                ForeColor = Color.FromArgb(89, 105, 113),
                Location = new Point(62, 338)
            };
            showPassword.CheckedChanged += delegate { passwordBox.UseSystemPasswordChar = !showPassword.Checked; };

            feedback = new Label
            {
                AutoSize = false,
                ForeColor = Color.FromArgb(183, 73, 62),
                Location = new Point(61, 367),
                Size = new Size(390, 38),
                TextAlign = ContentAlignment.MiddleLeft
            };
            Button signIn = CreatePrimaryButton("Masuk", 61, 414);
            signIn.Click += SignIn;
            LinkLabel registerLink = new LinkLabel
            {
                Text = "Belum punya akun? Daftar",
                LinkColor = Green,
                ActiveLinkColor = Green,
                VisitedLinkColor = Green,
                Font = new Font("Segoe UI", 10F, FontStyle.Bold),
                TextAlign = ContentAlignment.MiddleCenter,
                Location = new Point(61, 478),
                Size = new Size(390, 28)
            };
            registerLink.LinkClicked += OpenRegistration;

            content.Controls.Add(heading);
            content.Controls.Add(description);
            content.Controls.Add(usernameLabel);
            content.Controls.Add(usernameBox);
            content.Controls.Add(passwordLabel);
            content.Controls.Add(passwordBox);
            content.Controls.Add(showPassword);
            content.Controls.Add(feedback);
            content.Controls.Add(signIn);
            content.Controls.Add(registerLink);
            AcceptButton = signIn;
            Shown += delegate { usernameBox.Focus(); };
        }

        private void SignIn(object sender, EventArgs e)
        {
            string username = usernameBox.Text.Trim();
            string password = passwordBox.Text;
            if (username.Length == 0 || password.Length == 0)
            {
                feedback.Text = "Isi nama pengguna dan kata sandi.";
                return;
            }
            if (!accountStore.HasAccounts())
            {
                feedback.Text = "Belum ada akun. Daftar untuk mulai menggunakan aplikasi.";
                return;
            }
            if (!accountStore.Authenticate(username, password))
            {
                feedback.Text = "Nama pengguna atau kata sandi tidak cocok.";
                passwordBox.Clear();
                passwordBox.Focus();
                return;
            }

            AuthenticatedUsername = username;
            DialogResult = DialogResult.OK;
            Close();
        }

        private void OpenRegistration(object sender, LinkLabelLinkClickedEventArgs e)
        {
            using (RegistrationForm registration = new RegistrationForm(accountStore))
            {
                if (registration.ShowDialog(this) != DialogResult.OK) return;
                usernameBox.Text = registration.RegisteredUsername;
                passwordBox.Clear();
                feedback.ForeColor = Green;
                feedback.Text = "Akun berhasil dibuat. Silakan masuk.";
            }
        }

        private static Label CreateFieldLabel(string text, int x, int y)
        {
            return new Label
            {
                Text = text,
                Font = new Font("Segoe UI", 9.5F, FontStyle.Bold),
                ForeColor = Ink,
                Location = new Point(x, y),
                AutoSize = true
            };
        }

        private static TextBox CreateTextBox(int x, int y)
        {
            return new TextBox
            {
                Location = new Point(x, y),
                Size = new Size(390, 38),
                Font = new Font("Segoe UI", 12F),
                BorderStyle = BorderStyle.FixedSingle
            };
        }

        private static Button CreatePrimaryButton(string text, int x, int y)
        {
            Button button = new Button
            {
                Text = text,
                Location = new Point(x, y),
                Size = new Size(390, 46),
                BackColor = Green,
                ForeColor = Color.White,
                FlatStyle = FlatStyle.Flat,
                Font = new Font("Segoe UI", 11F, FontStyle.Bold),
                Cursor = Cursors.Hand
            };
            button.FlatAppearance.BorderSize = 0;
            return button;
        }

        internal static Icon LoadAppIcon()
        {
            string[] paths =
            {
                Path.Combine(AppDomain.CurrentDomain.BaseDirectory, "app.ico"),
                Path.GetFullPath(Path.Combine(AppDomain.CurrentDomain.BaseDirectory, "..", "..", "app.ico"))
            };
            foreach (string path in paths)
            {
                try { if (File.Exists(path)) return new Icon(path); }
                catch { }
            }
            return null;
        }

        private static Image LoadMascot()
        {
            using (Icon icon = LoadAppIcon())
            {
                return icon == null ? null : icon.ToBitmap();
            }
        }
    }

    internal sealed class RegistrationForm : Form
    {
        private static readonly Color Ink = Color.FromArgb(25, 39, 52);
        private static readonly Color Green = Color.FromArgb(0, 133, 97);
        private readonly NativeAccountStore accountStore;
        private readonly TextBox usernameBox;
        private readonly TextBox passwordBox;
        private readonly TextBox confirmBox;
        private readonly Label feedback;

        public string RegisteredUsername { get; private set; }

        public RegistrationForm(NativeAccountStore store)
        {
            accountStore = store;
            Text = "Buat akun - Celenganku";
            StartPosition = FormStartPosition.CenterParent;
            FormBorderStyle = FormBorderStyle.FixedDialog;
            MaximizeBox = false;
            MinimizeBox = false;
            ClientSize = new Size(520, 650);
            BackColor = Color.White;
            Font = new Font("Segoe UI", 10F);
            Icon appIcon = LoginForm.LoadAppIcon();
            if (appIcon != null) Icon = appIcon;

            Panel header = new Panel { Dock = DockStyle.Top, Height = 126, BackColor = Ink };
            PictureBox mascot = new PictureBox
            {
                Image = LoadMascot(),
                SizeMode = PictureBoxSizeMode.Zoom,
                Location = new Point(31, 28),
                Size = new Size(66, 66),
                BackColor = Color.Transparent
            };
            Label heading = new Label
            {
                Text = "Buat akun",
                Font = new Font("Segoe UI", 20F, FontStyle.Bold),
                ForeColor = Color.White,
                Location = new Point(117, 30),
                AutoSize = true
            };
            Label subtitle = new Label
            {
                Text = "Mulai bangun kebiasaan menabung.",
                Font = new Font("Segoe UI", 10F),
                ForeColor = Color.FromArgb(204, 220, 216),
                Location = new Point(119, 69),
                AutoSize = true
            };
            header.Controls.Add(mascot);
            header.Controls.Add(heading);
            header.Controls.Add(subtitle);
            Controls.Add(header);

            Label usernameLabel = CreateFieldLabel("Nama pengguna", 45, 158);
            usernameBox = CreateTextBox(45, 184);
            usernameBox.MaxLength = 20;
            Label usernameHint = CreateHint("4-20 karakter: huruf, angka, atau garis bawah.", 47, 220);
            Label passwordLabel = CreateFieldLabel("Kata sandi", 45, 261);
            passwordBox = CreateTextBox(45, 287);
            passwordBox.UseSystemPasswordChar = true;
            passwordBox.MaxLength = 32;
            Label passwordHint = CreateHint("8-32 karakter, huruf besar/kecil, angka, dan simbol.", 47, 323);
            Label confirmLabel = CreateFieldLabel("Ulangi kata sandi", 45, 364);
            confirmBox = CreateTextBox(45, 390);
            confirmBox.UseSystemPasswordChar = true;
            confirmBox.MaxLength = 32;
            CheckBox showPassword = new CheckBox
            {
                Text = "Tampilkan kata sandi",
                AutoSize = true,
                ForeColor = Color.FromArgb(89, 105, 113),
                Location = new Point(47, 431)
            };
            showPassword.CheckedChanged += delegate
            {
                passwordBox.UseSystemPasswordChar = !showPassword.Checked;
                confirmBox.UseSystemPasswordChar = !showPassword.Checked;
            };
            feedback = new Label
            {
                AutoSize = false,
                ForeColor = Color.FromArgb(183, 73, 62),
                Location = new Point(45, 460),
                Size = new Size(430, 46),
                TextAlign = ContentAlignment.MiddleLeft
            };
            Button register = new Button
            {
                Text = "Daftar",
                Location = new Point(45, 517),
                Size = new Size(430, 46),
                BackColor = Green,
                ForeColor = Color.White,
                FlatStyle = FlatStyle.Flat,
                Font = new Font("Segoe UI", 11F, FontStyle.Bold),
                Cursor = Cursors.Hand
            };
            register.FlatAppearance.BorderSize = 0;
            register.Click += Register;

            Controls.Add(usernameLabel);
            Controls.Add(usernameBox);
            Controls.Add(usernameHint);
            Controls.Add(passwordLabel);
            Controls.Add(passwordBox);
            Controls.Add(passwordHint);
            Controls.Add(confirmLabel);
            Controls.Add(confirmBox);
            Controls.Add(showPassword);
            Controls.Add(feedback);
            Controls.Add(register);
            AcceptButton = register;
            Shown += delegate { usernameBox.Focus(); };
        }

        private void Register(object sender, EventArgs e)
        {
            string username = usernameBox.Text.Trim();
            string password = passwordBox.Text;
            if (!Regex.IsMatch(username, "^[A-Za-z0-9_]{4,20}$"))
            {
                feedback.Text = "Nama pengguna harus 4-20 karakter (huruf, angka, garis bawah).";
                return;
            }
            if (!IsValidPassword(password))
            {
                feedback.Text = "Kata sandi belum memenuhi semua syarat.";
                return;
            }
            if (password != confirmBox.Text)
            {
                feedback.Text = "Ulangan kata sandi tidak cocok.";
                return;
            }

            string error;
            if (!accountStore.Register(username, password, out error))
            {
                feedback.Text = error;
                return;
            }
            RegisteredUsername = username;
            DialogResult = DialogResult.OK;
            Close();
        }

        private static bool IsValidPassword(string password)
        {
            if (password.Length < 8 || password.Length > 32) return false;
            bool upper = false;
            bool lower = false;
            bool digit = false;
            bool symbol = false;
            foreach (char character in password)
            {
                if (Char.IsWhiteSpace(character)) return false;
                if (Char.IsUpper(character)) upper = true;
                else if (Char.IsLower(character)) lower = true;
                else if (Char.IsDigit(character)) digit = true;
                else symbol = true;
            }
            return upper && lower && digit && symbol;
        }

        private static Label CreateFieldLabel(string text, int x, int y)
        {
            return new Label
            {
                Text = text,
                Font = new Font("Segoe UI", 9.5F, FontStyle.Bold),
                ForeColor = Ink,
                Location = new Point(x, y),
                AutoSize = true
            };
        }

        private static Label CreateHint(string text, int x, int y)
        {
            return new Label
            {
                Text = text,
                Font = new Font("Segoe UI", 8.5F),
                ForeColor = Color.FromArgb(89, 105, 113),
                Location = new Point(x, y),
                AutoSize = true
            };
        }

        private static TextBox CreateTextBox(int x, int y)
        {
            return new TextBox
            {
                Location = new Point(x, y),
                Size = new Size(430, 34),
                Font = new Font("Segoe UI", 11F),
                BorderStyle = BorderStyle.FixedSingle
            };
        }

        private static Image LoadMascot()
        {
            using (Icon icon = LoginForm.LoadAppIcon())
            {
                return icon == null ? null : icon.ToBitmap();
            }
        }
    }

    internal sealed class QuizForm : Form
    {
        private static readonly Color Ink = Color.FromArgb(25, 39, 52);
        private static readonly Color Green = Color.FromArgb(0, 133, 97);
        private readonly List<RadioButton> q1Choices = new List<RadioButton>();
        private readonly List<RadioButton> q2Choices = new List<RadioButton>();
        private readonly List<RadioButton> q3Choices = new List<RadioButton>();

        public QuizForm()
        {
            Text = "Kuis Literasi Keuangan - Celenganku";
            StartPosition = FormStartPosition.CenterParent;
            FormBorderStyle = FormBorderStyle.FixedDialog;
            MaximizeBox = false;
            MinimizeBox = false;
            ClientSize = new Size(700, 760);
            BackColor = Color.FromArgb(244, 247, 246);
            Font = new Font("Segoe UI", 10F);

            Icon appIcon = LoginForm.LoadAppIcon();
            if (appIcon != null) Icon = appIcon;

            Panel titlePanel = new Panel { Dock = DockStyle.Top, Height = 88, BackColor = Ink };
            Label title = new Label
            {
                Text = "Kuis Literasi Keuangan",
                Font = new Font("Segoe UI", 20F, FontStyle.Bold),
                ForeColor = Color.White,
                Location = new Point(24, 24),
                AutoSize = true
            };
            Label subtitle = new Label
            {
                Text = "Uji wawasan finansialmu dalam 3 pertanyaan.",
                Font = new Font("Segoe UI", 10F),
                ForeColor = Color.FromArgb(204, 220, 216),
                Location = new Point(26, 57),
                AutoSize = true
            };
            titlePanel.Controls.Add(title);
            titlePanel.Controls.Add(subtitle);
            Controls.Add(titlePanel);

            FlowLayoutPanel scrollArea = new FlowLayoutPanel
            {
                Dock = DockStyle.Fill,
                AutoScroll = true,
                Padding = new Padding(20, 18, 20, 20),
                FlowDirection = FlowDirection.TopDown,
                WrapContents = false
            };

            scrollArea.Controls.Add(CreateQuestionCard(1, "Berapa persentase ideal penghasilan yang disisihkan untuk tabungan setiap bulan?",
                new[] { "5% - 10%", "10% - 20%", "30% - 40%" }, q1Choices));
            scrollArea.Controls.Add(CreateQuestionCard(2, "Apa fungsi utama dari dana darurat?",
                new[] { "Untuk membeli aset baru", "Menyiapkan biaya tak terduga", "Untuk investasi jangka panjang" }, q2Choices));
            scrollArea.Controls.Add(CreateQuestionCard(3, "Metode menabung yang paling efektif biasanya adalah?",
                new[] { "Menabung setelah semua pengeluaran selesai", "Menyisihkan tabungan di awal sebelum belanja", "Menabung hanya saat ada sisa" }, q3Choices));

            Button submit = new Button
            {
                Text = "Lihat Skor",
                BackColor = Green,
                ForeColor = Color.White,
                FlatStyle = FlatStyle.Flat,
                Font = new Font("Segoe UI", 10F, FontStyle.Bold),
                Size = new Size(200, 42),
                Location = new Point(470, 700),
                Cursor = Cursors.Hand
            };
            submit.FlatAppearance.BorderSize = 0;
            submit.Click += SubmitQuiz;

            Controls.Add(scrollArea);
            Controls.Add(submit);
        }

        private void SubmitQuiz(object sender, EventArgs e)
        {
            int score = 0;
            if (IsSelected(q1Choices, 1)) score += 34;
            if (IsSelected(q2Choices, 1)) score += 33;
            if (IsSelected(q3Choices, 1)) score += 33;

            string category;
            if (score >= 90) category = "Pakar Keuangan (Sangat Baik)";
            else if (score >= 60) category = "Perencana Bijak (Baik)";
            else category = "Pemula (Perlu Peningkatan Literasi)";

            MessageBox.Show(this,
                "Skor Anda: " + score + " / 100\nKategori: " + category + "\nTips: Pertahankan kedisiplinan menabung dan kelola pengeluaran dengan bijak!",
                "Hasil Kuis",
                MessageBoxButtons.OK,
                MessageBoxIcon.Information);
        }

        private static bool IsSelected(List<RadioButton> choices, int index)
        {
            return choices.Count > index && choices[index].Checked;
        }

        private static Panel CreateQuestionCard(int number, string question, string[] answers, List<RadioButton> choices)
        {
            Panel panel = new Panel
            {
                Width = 630,
                Height = 190,
                BackColor = Color.White,
                Margin = new Padding(0, 0, 0, 12),
                Padding = new Padding(14)
            };
            Label questionLabel = new Label
            {
                Text = number + ". " + question,
                Font = new Font("Segoe UI", 11F, FontStyle.Bold),
                ForeColor = Ink,
                AutoSize = true,
                MaximumSize = new Size(590, 0)
            };
            panel.Controls.Add(questionLabel);

            int y = 46;
            for (int index = 0; index < answers.Length; index++)
            {
                RadioButton option = new RadioButton
                {
                    Text = answers[index],
                    Font = new Font("Segoe UI", 10F),
                    ForeColor = Ink,
                    Location = new Point(14, y),
                    AutoSize = true,
                    Tag = index
                };
                choices.Add(option);
                panel.Controls.Add(option);
                y += 28;
            }

            return panel;
        }
    }

    internal sealed class NativeAccountStore
    {
        private const int PasswordIterations = 120000;
        private readonly string accountsPath;
        private readonly JavaScriptSerializer serializer = new JavaScriptSerializer();
        private readonly object syncRoot = new object();

        public NativeAccountStore()
        {
            string dataDirectory = Path.Combine(Environment.GetFolderPath(Environment.SpecialFolder.LocalApplicationData), "Celenganku", "Data");
            Directory.CreateDirectory(dataDirectory);
            accountsPath = Path.Combine(dataDirectory, "accounts.json");
        }

        public bool HasAccounts()
        {
            lock (syncRoot) return LoadAccounts().Count > 0;
        }

        public bool Authenticate(string username, string password)
        {
            lock (syncRoot)
            {
                NativeAccount account = LoadAccounts().Find(delegate(NativeAccount item)
                {
                    return String.Equals(item.Username, username.Trim(), StringComparison.OrdinalIgnoreCase);
                });
                if (account == null) return false;
                try
                {
                    byte[] salt = Convert.FromBase64String(account.Salt);
                    byte[] expected = Convert.FromBase64String(account.PasswordHash);
                    byte[] actual = DerivePasswordHash(password, salt);
                    return FixedTimeEquals(expected, actual);
                }
                catch { return false; }
            }
        }

        public bool Register(string username, string password, out string error)
        {
            lock (syncRoot)
            {
                List<NativeAccount> accounts = LoadAccounts();
                if (accounts.Exists(delegate(NativeAccount item)
                {
                    return String.Equals(item.Username, username.Trim(), StringComparison.OrdinalIgnoreCase);
                }))
                {
                    error = "Nama pengguna sudah digunakan.";
                    return false;
                }

                byte[] salt = new byte[16];
                using (RandomNumberGenerator random = RandomNumberGenerator.Create()) random.GetBytes(salt);
                NativeAccount account = new NativeAccount
                {
                    Username = username.Trim(),
                    Salt = Convert.ToBase64String(salt),
                    PasswordHash = Convert.ToBase64String(DerivePasswordHash(password, salt)),
                    CreatedUtc = DateTime.UtcNow
                };
                accounts.Add(account);
                try
                {
                    string temporaryPath = accountsPath + ".tmp";
                    File.WriteAllText(temporaryPath, serializer.Serialize(accounts));
                    if (File.Exists(accountsPath)) File.Replace(temporaryPath, accountsPath, null);
                    else File.Move(temporaryPath, accountsPath);
                    error = null;
                    return true;
                }
                catch (Exception ex)
                {
                    error = "Akun tidak dapat disimpan: " + ex.Message;
                    return false;
                }
            }
        }

        private List<NativeAccount> LoadAccounts()
        {
            if (!File.Exists(accountsPath)) return new List<NativeAccount>();
            try
            {
                List<NativeAccount> accounts = serializer.Deserialize<List<NativeAccount>>(File.ReadAllText(accountsPath));
                return accounts ?? new List<NativeAccount>();
            }
            catch { return new List<NativeAccount>(); }
        }

        private static byte[] DerivePasswordHash(string password, byte[] salt)
        {
            using (Rfc2898DeriveBytes derivation = new Rfc2898DeriveBytes(password, salt, PasswordIterations))
            {
                return derivation.GetBytes(32);
            }
        }

        private static bool FixedTimeEquals(byte[] first, byte[] second)
        {
            if (first == null || second == null || first.Length != second.Length) return false;
            int difference = 0;
            for (int index = 0; index < first.Length; index++) difference |= first[index] ^ second[index];
            return difference == 0;
        }
    }

    public sealed class NativeAccount
    {
        public string Username { get; set; }
        public string Salt { get; set; }
        public string PasswordHash { get; set; }
        public DateTime CreatedUtc { get; set; }
    }
}