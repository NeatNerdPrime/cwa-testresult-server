import java.io.*;

class Dpkg {
  public static void main(String[] args) throws IOException {
    File dir = new File("/var/lib/dpkg/status.d/");
    PrintWriter pw = new PrintWriter("/var/lib/dpkg/status");
    String[] fileNames = dir.list();
    for (String fileName : fileNames) {
      System.out.println("Handling file: " + fileName);
      File f = new File(dir, fileName);
      BufferedReader br = new BufferedReader(new FileReader(f));
      String line = br.readLine();
      while (line != null) {
        pw.println(line);
        line = br.readLine();
      }
      pw.println();
      pw.flush();
    }
  }
}
