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
public class MotoHexFile extends HexFileBase{
  public MotoHexFile()
  {
    super();
  }
  public MotoHexFile(String filename) throws HexFileException
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
        if (line.charAt(0) != 'S')
        {
          throw new HexFileException("Invalid line", filename, lineOrig, lineIdx);
        }
        int addrLength;
        switch (line.charAt(1))
        {
          case '0': // comment block
            addrLength = 2;
            break;
          case '1': // 16 bit address block
            addrLength = 2;
            if (line.length() < 12)
              throw new HexFileException("Invalid record length", filename, lineOrig, lineIdx);
            break;
          case '2': // 24 bit address block
            addrLength = 3;
            if (line.length() < 14)
              throw new HexFileException("Invalid record length", filename, lineOrig, lineIdx);
            break;
          case '3': // 32 bit address block
            addrLength = 4;
            if (line.length() < 16)
              throw new HexFileException("Invalid record length", filename, lineOrig, lineIdx);
            break;
          case '5': // 16 bit record count block
            addrLength = 2;
            if (line.length() != 10)
              throw new HexFileException("Invalid record length", filename, lineOrig, lineIdx);
            break;
          case '9': // end filerecord with 16 bit address block
            addrLength = 2;
            if (line.length() != 10)
              throw new HexFileException("Invalid record length", filename, lineOrig, lineIdx);
            break;
          default:
             throw new HexFileException("Invalid record type", filename, lineOrig, lineIdx);
        }
        byte[] lineData;
        try {
          lineData = convertRecord(line.substring(2));
        }catch (HexFileException e) {
          throw new HexFileException(e.Error, filename, lineOrig, lineIdx);
        }
        byte chksum = checksum(lineData, lineData.length - 1);
        chksum = (byte)(0xff - chksum);
        if (chksum != lineData[lineData.length - 1])
           throw new HexFileException("Invalid checksum", filename, lineOrig, lineIdx);
        int recLen = byte2int(lineData[0]);
        if ((lineData.length - 1) != recLen)
           throw new HexFileException("Invalid record length data ", filename, lineOrig, lineIdx);
        int addr = (byte2int(lineData[1]) << 8) + byte2int(lineData[2]);
        if (addrLength > 2)
          addr = (addr << 8) + byte2int(lineData[3]);
        if (addrLength > 3)
          addr = (addr << 8) + byte2int(lineData[4]);
        if ((line.charAt(1) == '1') || (line.charAt(1) == '2') || (line.charAt(1) == '3'))
        {
          byte[] dataRec = new byte[lineData.length - addrLength - 2];
          java.lang.System.arraycopy(lineData, addrLength + 1, dataRec, 0, dataRec.length);
          InsertRecord(addr, dataRec);
        }
        lineIdx++;
      }
    }catch (IOException e) {
      throw new HexFileException("Unable to read line", filename, null, lineIdx);
    }
    pack();
  }
}
