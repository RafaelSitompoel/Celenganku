using System;
using System.Diagnostics;
using System.Drawing;
using System.IO;
using System.Security.Principal;
using System.Windows.Forms;
using Microsoft.Win32;
using Microsoft.VisualBasic.FileIO;

namespace CelengankuNativeUninstaller
{
    internal static class UninstallerProgram
    {
        [STAThread]
        private static void Main(string[] args)
        {
            Application.EnableVisualStyles();
            Application.SetCompatibleTextRenderingDefault(false);
            if (args.Length == 3 && args[0] == "--move-to-recycle-bin")
            {
                FinishUninstall(args[1], args[2]);
                return;
            }

            if (!IsAdministrator())
            {
                try
                {
                    Process.Start(new ProcessStartInfo { FileName = Application.ExecutablePath, Verb = "runas", UseShellExecute = true });
                }
                catch { }
                return;
            }

            DialogResult answer = MessageBox.Show(
                "Hapus aplikasi Celenganku dari Windows?\n\nFile tabungan dan backup Anda di AppData tidak akan dihapus.",
                "Uninstall Celenganku",
                MessageBoxButtons.YesNo,
                MessageBoxIcon.Warning);
            if (answer != DialogResult.Yes) return;

            string installDirectory = Path.GetDirectoryName(Application.ExecutablePath);
            StopApplication();
            string helperPath = Path.Combine(Path.GetTempPath(), "Celenganku-Uninstall-" + Guid.NewGuid().ToString("N") + ".exe");
            try
            {
                File.Copy(Application.ExecutablePath, helperPath);
                Process.Start(new ProcessStartInfo
                {
                    FileName = helperPath,
                    Arguments = "--move-to-recycle-bin " + Process.GetCurrentProcess().Id + " \"" + installDirectory + "\"",
                    CreateNoWindow = true,
                    UseShellExecute = false,
                    WindowStyle = ProcessWindowStyle.Hidden
                });
                MessageBox.Show("Folder aplikasi akan dipindahkan ke Recycle Bin setelah jendela ini ditutup. Data pribadi di AppData tetap tersimpan.", "Uninstall Celenganku", MessageBoxButtons.OK, MessageBoxIcon.Information);
            }
            catch (Exception ex)
            {
                try { if (File.Exists(helperPath)) File.Delete(helperPath); } catch { }
                MessageBox.Show("Uninstaller tidak dapat dijalankan: " + ex.Message, "Uninstall gagal", MessageBoxButtons.OK, MessageBoxIcon.Error);
            }
        }

        private static void FinishUninstall(string parentProcessId, string installDirectory)
        {
            int processId;
            if (!Int32.TryParse(parentProcessId, out processId)) return;
            try
            {
                using (Process parent = Process.GetProcessById(processId)) parent.WaitForExit();
            }
            catch { }

            bool uninstallCompleted = false;
            try
            {
                MoveDirectoryToRecycleBin(installDirectory);
                uninstallCompleted = true;
            }
            catch (Exception ex)
            {
                DialogResult deletePermanently = MessageBox.Show(
                    "Folder aplikasi tidak dapat dipindahkan ke Recycle Bin.\n\n" + ex.Message + "\n\nHapus permanen folder aplikasi? Data tabungan di AppData tetap aman.",
                    "Recycle Bin tidak tersedia",
                    MessageBoxButtons.YesNo,
                    MessageBoxIcon.Warning);
                if (deletePermanently == DialogResult.Yes)
                {
                    try
                    {
                        if (Directory.Exists(installDirectory)) Directory.Delete(installDirectory, true);
                        uninstallCompleted = true;
                    }
                    catch (Exception deleteException)
                    {
                        MessageBox.Show("Folder aplikasi tidak dapat dihapus: " + deleteException.Message, "Uninstall gagal", MessageBoxButtons.OK, MessageBoxIcon.Error);
                    }
                }
            }

            if (uninstallCompleted)
            {
                try
                {
                    RemoveShortcuts();
                    Registry.LocalMachine.DeleteSubKeyTree(@"SOFTWARE\Microsoft\Windows\CurrentVersion\Uninstall\Celenganku", false);
                }
                catch (Exception ex)
                {
                    MessageBox.Show("Aplikasi sudah dihapus, tetapi shortcut atau entri uninstall tidak dapat dibersihkan: " + ex.Message, "Pembersihan belum lengkap", MessageBoxButtons.OK, MessageBoxIcon.Warning);
                }
            }

            DeleteHelperAfterExit();
        }

        private static void DeleteHelperAfterExit()
        {
            string command = "/c choice /C Y /N /D Y /T 2 >nul & del /f /q \"" + Application.ExecutablePath + "\"";
            Process.Start(new ProcessStartInfo
            {
                FileName = Environment.GetEnvironmentVariable("ComSpec"),
                Arguments = command,
                CreateNoWindow = true,
                UseShellExecute = false,
                WindowStyle = ProcessWindowStyle.Hidden
            });
        }

        private static void MoveDirectoryToRecycleBin(string directoryPath)
        {
            if (!Directory.Exists(directoryPath)) return;
            FileSystem.DeleteDirectory(directoryPath, UIOption.OnlyErrorDialogs, RecycleOption.SendToRecycleBin);
            if (Directory.Exists(directoryPath)) throw new IOException("Folder instalasi masih ada setelah operasi Recycle Bin.");
        }

        private static bool IsAdministrator()
        {
            WindowsIdentity identity = WindowsIdentity.GetCurrent();
            return new WindowsPrincipal(identity).IsInRole(WindowsBuiltInRole.Administrator);
        }

        private static void StopApplication()
        {
            foreach (Process process in Process.GetProcessesByName("Celenganku"))
            {
                try
                {
                    process.CloseMainWindow();
                    if (!process.WaitForExit(5000)) process.Kill();
                    process.WaitForExit();
                }
                catch { }
                finally { process.Dispose(); }
            }
        }

        private static void RemoveShortcuts()
        {
            string startMenu = Path.Combine(Environment.GetFolderPath(Environment.SpecialFolder.CommonPrograms), "Celenganku");
            try { if (Directory.Exists(startMenu)) Directory.Delete(startMenu, true); } catch { }
            string desktopShortcut = Path.Combine(Environment.GetFolderPath(Environment.SpecialFolder.CommonDesktopDirectory), "Celenganku.lnk");
            try { if (File.Exists(desktopShortcut)) File.Delete(desktopShortcut); } catch { }
        }
    }
}