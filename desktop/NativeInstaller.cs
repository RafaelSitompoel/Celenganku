using System;
using System.Diagnostics;
using System.Drawing;
using System.IO;
using System.Reflection;
using System.Security.Principal;
using System.Windows.Forms;
using Microsoft.Win32;

namespace CelengankuNativeInstaller
{
    internal static class InstallerProgram
    {
        [STAThread]
        private static void Main()
        {
            Application.EnableVisualStyles();
            Application.SetCompatibleTextRenderingDefault(false);
            if (!IsAdministrator())
            {
                try
                {
                    Process.Start(new ProcessStartInfo { FileName = Application.ExecutablePath, Verb = "runas", UseShellExecute = true });
                }
                catch { MessageBox.Show("Instalasi Celenganku memerlukan izin Administrator.", "Izin diperlukan", MessageBoxButtons.OK, MessageBoxIcon.Warning); }
                return;
            }
            Application.Run(new InstallerForm());
        }

        private static bool IsAdministrator()
        {
            WindowsIdentity identity = WindowsIdentity.GetCurrent();
            return new WindowsPrincipal(identity).IsInRole(WindowsBuiltInRole.Administrator);
        }
    }

    internal sealed class InstallerForm : Form
    {
        private readonly string installDirectory;
        private readonly CheckBox desktopShortcut;
        private readonly CheckBox launchWhenDone;
        private readonly Label status;
        private readonly ProgressBar progress;
        private readonly Button installButton;

        public InstallerForm()
        {
            installDirectory = Path.Combine(Environment.GetFolderPath(Environment.SpecialFolder.ProgramFiles), "Celenganku");
            Text = "Installer Celenganku untuk Windows 11";
            StartPosition = FormStartPosition.CenterScreen;
            FormBorderStyle = FormBorderStyle.FixedDialog;
            MaximizeBox = false;
            MinimizeBox = false;
            ClientSize = new Size(540, 350);
            BackColor = Color.FromArgb(244, 247, 246);
            Font = new Font("Segoe UI", 10F);
            try
            {
                using (Stream icon = Assembly.GetExecutingAssembly().GetManifestResourceStream("app.ico"))
                    if (icon != null) Icon = new Icon(icon);
            }
            catch { }

            Label title = new Label { Text = "Celenganku untuk Windows 11", Font = new Font("Segoe UI", 18F, FontStyle.Bold), ForeColor = Color.FromArgb(25, 39, 52), Location = new Point(28, 24), AutoSize = true };
            Label description = new Label { Text = "Aplikasi tabungan native akan dipasang ke folder Program Files.", ForeColor = Color.FromArgb(81, 99, 109), Location = new Point(31, 67), AutoSize = true };
            Label installPath = new Label { Text = installDirectory, ForeColor = Color.FromArgb(0, 112, 83), Font = new Font("Segoe UI", 9F, FontStyle.Bold), Location = new Point(31, 99), AutoSize = true };
            desktopShortcut = new CheckBox { Text = "Buat shortcut di Desktop", Checked = true, AutoSize = true, Location = new Point(32, 146) };
            launchWhenDone = new CheckBox { Text = "Buka Celenganku setelah pemasangan", Checked = true, AutoSize = true, Location = new Point(32, 176) };
            progress = new ProgressBar { Location = new Point(31, 225), Size = new Size(476, 16), Visible = false };
            status = new Label { Text = "Aplikasi dan uninstaller akan dipasang bersama.", Location = new Point(31, 248), Size = new Size(476, 20), ForeColor = Color.FromArgb(81, 99, 109) };
            installButton = new Button { Text = "Pasang Celenganku", Location = new Point(31, 284), Size = new Size(476, 42), BackColor = Color.FromArgb(0, 133, 97), ForeColor = Color.White, FlatStyle = FlatStyle.Flat, Font = new Font("Segoe UI", 10F, FontStyle.Bold) };
            installButton.FlatAppearance.BorderSize = 0;
            installButton.Click += Install;
            Controls.Add(title);
            Controls.Add(description);
            Controls.Add(installPath);
            Controls.Add(desktopShortcut);
            Controls.Add(launchWhenDone);
            Controls.Add(progress);
            Controls.Add(status);
            Controls.Add(installButton);
        }

        private void Install(object sender, EventArgs e)
        {
            installButton.Enabled = false;
            progress.Visible = true;
            try
            {
                Directory.CreateDirectory(installDirectory);
                progress.Value = 15;
                status.Text = "Menyalin aplikasi native...";
                ExtractResource("Celenganku.exe", Path.Combine(installDirectory, "Celenganku.exe"));
                progress.Value = 45;
                ExtractResource("Uninstall-Celenganku.exe", Path.Combine(installDirectory, "Uninstall-Celenganku.exe"));
                ExtractResource("app.ico", Path.Combine(installDirectory, "app.ico"));
                progress.Value = 70;
                CreateShortcuts();
                RegisterUninstall();
                progress.Value = 100;
                status.Text = "Pemasangan selesai.";
                if (launchWhenDone.Checked)
                    Process.Start(new ProcessStartInfo
                    {
                        FileName = "explorer.exe",
                        Arguments = "\"" + Path.Combine(installDirectory, "Celenganku.exe") + "\"",
                        UseShellExecute = true
                    });
                MessageBox.Show("Celenganku berhasil dipasang di:\n" + installDirectory + "\n\nData pengguna akan disimpan terpisah di AppData.", "Pemasangan selesai", MessageBoxButtons.OK, MessageBoxIcon.Information);
                Close();
            }
            catch (Exception ex)
            {
                status.Text = "Pemasangan gagal.";
                installButton.Enabled = true;
                MessageBox.Show("Instalasi gagal: " + ex.Message, "Kesalahan instalasi", MessageBoxButtons.OK, MessageBoxIcon.Error);
            }
        }

        private void ExtractResource(string resourceName, string destination)
        {
            using (Stream stream = Assembly.GetExecutingAssembly().GetManifestResourceStream(resourceName))
            {
                if (stream == null) throw new FileNotFoundException("Payload installer tidak ditemukan: " + resourceName);
                using (FileStream output = new FileStream(destination, FileMode.Create, FileAccess.Write)) stream.CopyTo(output);
            }
        }

        private void CreateShortcuts()
        {
            string startMenu = Path.Combine(Environment.GetFolderPath(Environment.SpecialFolder.CommonPrograms), "Celenganku");
            Directory.CreateDirectory(startMenu);
            CreateShortcut(Path.Combine(startMenu, "Celenganku.lnk"), "Celenganku");
            CreateShortcut(Path.Combine(startMenu, "Uninstall Celenganku.lnk"), "Uninstall-Celenganku");
            if (desktopShortcut.Checked)
                CreateShortcut(Path.Combine(Environment.GetFolderPath(Environment.SpecialFolder.CommonDesktopDirectory), "Celenganku.lnk"), "Celenganku");
        }

        private void CreateShortcut(string path, string executable)
        {
            Type shellType = Type.GetTypeFromProgID("WScript.Shell");
            if (shellType == null) return;
            dynamic shell = Activator.CreateInstance(shellType);
            dynamic shortcut = shell.CreateShortcut(path);
            shortcut.TargetPath = Path.Combine(installDirectory, executable + ".exe");
            shortcut.WorkingDirectory = installDirectory;
            shortcut.IconLocation = Path.Combine(installDirectory, "app.ico") + ",0";
            shortcut.Description = "Aplikasi Celenganku untuk Windows 11";
            shortcut.Save();
        }

        private void RegisterUninstall()
        {
            using (RegistryKey key = Registry.LocalMachine.CreateSubKey(@"SOFTWARE\Microsoft\Windows\CurrentVersion\Uninstall\Celenganku"))
            {
                if (key == null) throw new InvalidOperationException("Entri uninstall Windows tidak dapat dibuat.");
                key.SetValue("DisplayName", "Celenganku");
                key.SetValue("DisplayVersion", "1.0.0");
                key.SetValue("Publisher", "Rafael Paulus Sitompul");
                key.SetValue("InstallLocation", installDirectory);
                key.SetValue("DisplayIcon", Path.Combine(installDirectory, "app.ico"));
                key.SetValue("UninstallString", "\"" + Path.Combine(installDirectory, "Uninstall-Celenganku.exe") + "\"");
                key.SetValue("NoModify", 1, RegistryValueKind.DWord);
                key.SetValue("NoRepair", 1, RegistryValueKind.DWord);
            }
        }
    }
}