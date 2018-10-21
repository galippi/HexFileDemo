/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hexfiledemo;

import hexfiledemo.HexFile.HexFileBase;
import hexfiledemo.HexFile.HexFileException;
import hexfiledemo.HexFile.HexFileRecord;
import hexfiledemo.HexFile.IntelHexFile;
import hexfiledemo.HexFile.MotoHexFile;

/**
 *
 * @author liptakok
 */
public class HexFileDemo {

  static void printHexFile(HexFileBase file)
  {
      for (int i = 0; i < file.data.size(); i++)
      {
        HexFileRecord rec = file.data.get(i);
        System.out.println("line: " + i + " addr=" + rec.getAddress() + " len=" + rec.size());
        String str = "";
        byte[] data = rec.getData();
        for (int j = 0; j < rec.size(); j++)
        {
          str = str + byte2hex(data[j]);
        }
        System.out.println("  data:" + str);
      }
  }
  /**
   * @param args the command line arguments
   */
  public static void main(String[] args) {
    try {
      MotoHexFile file = new MotoHexFile("a4.s19");
      printHexFile(file);
      IntelHexFile file2 = new IntelHexFile("b1.hex");
      printHexFile(file2);
    }catch (HexFileException e) {
      System.out.println("Error: " + e.toString());
    }
  }
  static char byte2hexAscii(byte data)
  {
    if (data < 10)
      return (char)('0' + data);
    else
      return (char)('A' + data - 10);
  }
  static String byte2hex(byte data)
  {
    String str = "" + byte2hexAscii((byte)((data >> 4) & 0x0f)) + byte2hexAscii((byte)((data >> 0) & 0x0f));
    return str;
  }
}
