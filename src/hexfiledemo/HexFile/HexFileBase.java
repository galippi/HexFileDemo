/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hexfiledemo.HexFile;

import java.util.ArrayList;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 *
 * @author liptakok
 */
public class HexFileBase {
  public HexFileBase()
  {
    data = new ArrayList<>();
  }
  public void InsertRecord(int address, byte[] data)
  {
    boolean done = false;
    for (int i = 0; i < this.data.size(); i++)
    {
      if (this.data.get(i).insert(address, data))
      {
        done = true;
        break;
      }
    }
    if (!done)
    {
      this.data.add(new HexFileRecord(address, data));
    }
  }
  byte hex2dec(char ch) throws HexFileException
  {
    if ((ch >= '0') && (ch <= '9'))
    {
      return (byte)(ch - '0');
    }else
    if ((ch >= 'A') && (ch <= 'F'))
    {
      return (byte)(ch - 'A' + 10);
    }else
    if ((ch >= 'a') && (ch <= 'f'))
    {
      return (byte)(ch - 'a' + 10);
    }
    throw new HexFileException("Invalid character", null, null, -1);
  }
  public byte[] convertRecord(String dataStr) throws HexFileException
  {
    int len = dataStr.length();
    if ((len % 2) != 0)
      throw new HexFileException("Invalid line length", null, null, -1);
    byte[] data = new byte[len/2];
    for (int i = 0; i < len; i += 2)
    {
      data[i / 2] = (byte)((hex2dec(dataStr.charAt(i)) * 16) + hex2dec(dataStr.charAt(i + 1)));
    }
    return data;
  }
  int byte2int(byte data)
  {
    int d = data;
    if (d < 0) d = 256 + d;
    return d;
  }
  byte checksum(byte[] lineData, int len)
  {
    byte chksum = 0;
    for (int i = 0; i < len; i++)
      chksum = (byte)(chksum + lineData[i]);
    return chksum;
  }
  void pack()
  {
    boolean modified = true;
    while(modified)
    {
      modified = false;
      for(int i = 0; (i < size() - 1) && !modified; i++)
      {
        for(int j = i + 1; (j < size()) && !modified; j++)
        {
          if (data.get(i).end == data.get(j).address)
          {
            modified = true;
            HexFileRecord old = data.get(j);
            data.remove(old);
            InsertRecord(old.address, old.getData());
          }
        }
      }
    }
  }
  public HexFileBase compare(HexFileBase other)
  {
    HexFileBase result = new HexFileBase();
    SortedSet set = new TreeSet();
    for (int i = 0; i < size(); i++)
    {
      set.add(new HexBlockHeader(data.get(i).address, data.get(i).size()));
    }
    for (int i = 0; i < other.size(); i++)
    {
      set.add(new HexBlockHeader(other.get(i).address, other.get(i).size()));
    }
    return result;
  }
  public int size()
  {
    return data.size();
  }
  public HexFileRecord get(int idx)
  {
    return data.get(idx);
  }
  public void load(String filename) throws HexFileException
  {
    throw new HexFileException("Invalid call of load of HexFileBase", "", "", -1);
  }
  public ArrayList<HexFileRecord> data;
}
