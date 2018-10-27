/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hexfiledemo;

import hexfiledemo.HexFile.HexFile;
import hexfiledemo.HexFile.HexFileBase;
import hexfiledemo.HexFile.HexFileException;
import hexfiledemo.HexFile.HexFileRecord;
import hexfiledemo.HexFile.MotoHexFile;
import java.util.Iterator;
import java.util.Map;

/**
 *
 * @author liptakok
 */
public class HexFileDemo {

  static void printHexFile(HexFileBase file)
  {
      Iterator i = file.getIterator();
      while(i.hasNext()) {
        Map.Entry me = (Map.Entry)i.next();
        HexFileRecord rec = (HexFileRecord)me.getValue();
        System.out.println("Record: " + i + " addr=" + rec.getAddress() + " len=" + rec.size());
        String str = "";
        byte[] data = rec.getData();
        for (int j = 0; j < rec.size(); j++)
        {
          str = str + byte2hex(data[j]);
        }
        System.out.println("  data:" + str);
      }
  }
  static void TestWithException(String filename, String ExceptionText) throws HexFileException
  {
    try {
      new MotoHexFile(filename);
      throw new HexFileException("Test case is failed - no exception", filename, "", -1);
    }catch (HexFileException e) {
      if (e.Error.contentEquals(ExceptionText))
      {
        System.out.println("Test case ok " + filename + "!");
      }else
      {
        throw new HexFileException("Test case is failed - no exception", filename, "", -1);
      }
    }
  }
  static void TestContent(String filename, int address, byte[] data) throws HexFileException
  {
    HexFileBase file = new HexFile(filename);
    if (file.size() != 1)
      throw new HexFileException("Test case is failed - block count failure", filename, "", -1);
    HexFileRecord rec = file.getFirst();
    if (rec.getAddress() != address)
      throw new HexFileException("Test case is failed - address failure", filename, "", -1);
    if (rec.size() != data.length)
      throw new HexFileException("Test case is failed - size failure", filename, "", -1);
    byte[] recData = rec.getData();
    for (int i = 0; i < data.length; i++)
      if (data[i] != recData[i])
        throw new HexFileException("Test case is failed - data failure", filename, "", -1);
    System.out.println("Test case ok " + filename + "!");
  }
  /**
   * @param args the command line arguments
   */
  public static void main(String[] args) {
    try {
      HexFileBase fileHex = new HexFile("b2.hex");
      fileHex.initIterator();
      HexFileRecord rec;
      while((rec = fileHex.getNext()) != null) {
        byte[] data = rec.getData();
        if ((data.length % 4) != 0)
          throw new HexFileException("Data record must be 32 bits wide!", "", "", -1);
        for(int j = 0; j < data.length; j += 4)
        {
          byte tmp = data[j];
          data[j] = data[j + 3];
          data[j + 3] = tmp;
          tmp = data[j +1];
          data[j + 1] = data[j + 2];
          data[j + 2] = tmp;
        }
      }
      printHexFile(fileHex);
      HexFileBase fileS19 = new HexFile("b2.s19");
      printHexFile(fileS19);
      TestWithException("TestData/a1.s19", "Invalid checksum"); // invalid checksum report
      byte[] data1 = {0x00, 0x5A, (byte)0xA5, (byte)0xFF};
      TestContent("TestData/a2.s19",     0x0000, data1);  // basic file load test - S1 record
      TestContent("TestData/a3.s19",     0x0000, data1); // insertRecord test - S1 record
      TestContent("TestData/a4.s19",     0x0000, data1); // pack testing - S1 record
      TestContent("TestData/a5.s19",     0x89AB, data1); // pack testing - S1 record
      TestContent("TestData/a6.s19",   0x123456, data1); // pack testing - S2 record
      TestContent("TestData/a7.s19", 0x87654321, data1); // pack testing - S3 record
      TestContent("TestData/i2.hex",     0x0000, data1);  // basic file load test - intel hex record
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
