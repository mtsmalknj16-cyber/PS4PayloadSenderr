# PS4 Payload Sender (Android)

Arabic Android app to select a local file and send its raw bytes over TCP to a PS4 IP and port (default 9020). Reset clears the selected file, IP, and port.

## Build an APK using GitHub Actions
1. Create a GitHub repository and upload the contents of this folder to the repository root.
2. Open the **Actions** tab and enable workflows if prompted.
3. Select **Build Android APK** and press **Run workflow**.
4. When the workflow completes, open its run, scroll to **Artifacts**, and download `PS4-Payload-Sender-APK`.
5. Extract the ZIP and install `app-debug.apk` on your Android phone. Android may ask you to allow installation from that source.

The app sends the file bytes to the chosen TCP endpoint. It does not exploit the console or verify payload compatibility. Use only payloads you trust and only on devices you own or are authorized to test. Keep phone and console on a reachable network; VPNs and guest Wi-Fi isolation can block the connection.
