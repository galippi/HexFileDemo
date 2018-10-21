/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hexfiledemo.HexFile;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

/**
 *
 * @author liptakok
 */
public class IntelHexFile extends HexFileBase{
  public IntelHexFile()
  {
    super();
  }
  public IntelHexFile(String filename) throws HexFileException
  {
    super();
    load(filename);
  }
  public void load(String filename) throws HexFileException
  {
    BufferedReader fin;
    try {
      fin = new BufferedReader(new FileReader(filename));
    }catch (IOException e) {
      throw new HexFileException("Unable to open file", filename, null, -1);
    }
    String line;
    int lineIdx = 1;
    int addrBase = 0;
    try {
      while ((line = fin.readLine()) != null)
      {
        String lineOrig = line;
        line = line.trim();
        line = line.toUpperCase();
        if (line.length() < 8)
        {
          throw new HexFileException("Too short line", filename, lineOrig, lineIdx);
        }
        if (line.charAt(0) != ':')
        {
          throw new HexFileException("Invalid line", filename, lineOrig, lineIdx);
        }
        byte[] lineData;
        try {
          lineData = convertRecord(line.substring(1));
        }catch (HexFileException e) {
          throw new HexFileException(e.Error, filename, lineOrig, lineIdx);
        }
        int recLen = byte2int(lineData[0]);
        if (lineData.length != (recLen + 5))
           throw new HexFileException("Invalid record length data ", filename, lineOrig, lineIdx);
        byte chksum = 0;
        for (int i = 0; i < lineData.length - 1; i++)
          chksum = (byte)(chksum + lineData[i]);
        chksum = (byte)(256 - chksum);
        if (chksum != lineData[lineData.length - 1])
           throw new HexFileException("Invalid checksum", filename, lineOrig, lineIdx);
        int addr = (byte2int(lineData[1]) << 8) + byte2int(lineData[2]);
        switch (byte2int(lineData[3]))
        {
          case 0: // data block
            addr = addr + addrBase;
            byte[] dataRec = new byte[lineData.length - 5];
            java.lang.System.arraycopy(lineData, 4, dataRec, 0, dataRec.length);
            InsertRecord(addr, dataRec);
            break;
          case 1: // end of file record
            if (line.length() != 15)
              throw new HexFileException("Invalid record length", filename, lineOrig, lineIdx);
            break;
          case 2: // 20 bit address block
            addrBase = addr << 4;
            addr = addrBase;
            if (line.length() < 12)
              throw new HexFileException("Invalid record length", filename, lineOrig, lineIdx);
            break;
          case 4: // extended linear address record
            addrBase = (byte2int(lineData[4]) << 8) + byte2int(lineData[5]) << 16;
            if (line.length() != 15)
              throw new HexFileException("Invalid record length", filename, lineOrig, lineIdx);
            break;
          default:
             throw new HexFileException("Invalid record type", filename, lineOrig, lineIdx);
        }
        lineIdx++;
      }
    }catch (IOException e) {
      throw new HexFileException("Unable to read line", filename, null, lineIdx);
    }
  }
}
