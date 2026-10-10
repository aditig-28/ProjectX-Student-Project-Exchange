# Optional Demonstration Guide: Oracle ODBC Connectivity

## 1. Technical Clarification: JDBC vs. ODBC
- **ProjectX Web Application Architecture**:
  The ProjectX Java web application uses **JDBC (Java Database Connectivity)** via the Oracle Thin Driver (`ojdbc11.jar`), which is a 100% pure Java (Type 4) driver communicating directly with Oracle Database 21c XE over TCP/IP port 1521. It does **not** rely on ODBC.
- **Why this guide exists**:
  Certain university syllabi require demonstrating **ODBC (Open Database Connectivity)** alongside JDBC to show proficiency with native C-level database drivers and Windows Data Source Names (DSN). This guide explains how to set up an Oracle ODBC DSN for demonstration purposes.

---

## 2. Prerequisites for Oracle ODBC
To configure an ODBC Data Source for Oracle Database on Windows:
1. **Oracle ODBC Driver**:
   Already included with your Oracle Database 21c XE installation under `C:\app\oracle\dbhomeXE\bin\SQORA32.DLL` (or 64-bit `sqora.dll`).
2. **Oracle TNS Listener**:
   Must be running on port 1521 (verified as `OracleOraDB21Home1TNSListener`).
3. **Target Database**:
   Oracle XE PDB `XEPDB1` on `localhost:1521/XEPDB1`.

---

## 3. Step-by-Step DSN Setup (Windows ODBC Data Source Administrator)

1. Press `Win + R`, type `odbcad32.exe` and press **Enter** (this opens the **64-bit ODBC Data Source Administrator**).
2. Select the **System DSN** tab (to make the DSN available to all system services) or **User DSN**.
3. Click **Add...**.
4. In the list of installed drivers, select:
   **Oracle in dbhomeXE** (or `Oracle in OraDB21Home1`).
5. Click **Finish**. The *Oracle ODBC Driver Configuration* dialog will appear.
6. Enter the following parameters:
   - **Data Source Name**: `ProjectX_Oracle_DSN`
   - **Description**: `ODBC DSN for ProjectX College Demonstration`
   - **TNS Service Name**: `localhost:1521/XEPDB1` (or select `XEPDB1` from dropdown)
   - **User ID**: `PROJECTX`
7. Click **Test Connection**:
   - In the password prompt, enter: `projectx123`
   - Click **OK**.
   - You should receive the dialog: **"Connection successful!"**
8. Click **OK** to save the DSN.

---

## 4. Testing the ODBC Connection via PowerShell / Script

You can verify the configured DSN directly using Windows PowerShell without installing third-party tools:

```powershell
# Test Oracle ODBC Connection using .NET System.Data.Odbc
$connStr = "Dsn=ProjectX_Oracle_DSN;Uid=PROJECTX;Pwd=projectx123;"
$conn = New-Object System.Data.Odbc.OdbcConnection($connStr)

try {
    $conn.Open()
    Write-Host "ODBC Connection Successful!" -ForegroundColor Green

    $cmd = New-Object System.Data.Odbc.OdbcCommand("SELECT title, category FROM projects", $conn)
    $reader = $cmd.ExecuteReader()

    Write-Host "`nFetched Projects via ODBC:"
    while ($reader.Read()) {
        Write-Host "• $($reader['title']) [Category: $($reader['category'])]"
    }
} catch {
    Write-Host "ODBC Error: $_" -ForegroundColor Red
} finally {
    $conn.Close()
}
```

---

## 5. Summary Comparison for Viva Voce

| Criterion | JDBC (Used in ProjectX Web App) | ODBC (Optional External Tooling) |
| :--- | :--- | :--- |
| **Primary Platform** | Java / JVM | C, C++, Microsoft Access, Excel, BI tools |
| **Driver Architecture** | Type 4 Pure Java (network protocol) | Native C library (`.dll` on Windows, `.so` on Linux) |
| **System Configuration** | Self-contained in application JAR (`ojdbc11`) | Requires OS registry entry / Windows DSN |
| **Performance in Servlets**| Extremely high (native JVM threads & pooling) | Lower (requires JNI bridging overhead) |
