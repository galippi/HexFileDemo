/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hexfiledemo.HexFile;

import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

/**
 *
 * @author liptakok
 */
public class HexFileBase {
  public HexFileBase()
  {
    beginList = new TreeMap();
    endList = new TreeMap();
  }
  public void InsertRecord(HexFileRecord rec)
  {
    InsertRecord(rec.address, rec.getData());
  }
  public void InsertRecord(int address, byte[] data)
  {
    int endAddress = address + data.length;
    HexFileRecord rec = (HexFileRecord)endList.get(address);
    if (rec != null)
    {
      if (rec.insert(address, data))
      {
        endList.remove(address);
        endList.put(endAddress, rec);
      }else
        rec = null;
    }
    if (rec == null)
    {
      rec = (HexFileRecord)beginList.get(endAddress);
      if (rec != null)
      {
        if (rec.insert(address, data))
        {
          beginList.remove(endAddress);
          beginList.put(address, rec);
        }else
          rec = null;
      }
    }
    if (rec == null)
    {
      rec = new HexFileRecord(address, data);
      beginList.put(address, rec);
      endList.put(endAddress, rec);
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
      Iterator i = beginList.entrySet().iterator();
      while(i.hasNext()) {
        Map.Entry me = (Map.Entry)i.next();
        int address = (int)me.getKey();
        HexFileRecord rec = (HexFileRecord)me.getValue();
        int endAddress = rec.end;
        HexFileRecord recDest = (HexFileRecord)endList.get(address);
        if (recDest != null)
        {
          if (recDest.insert(rec))
          {
            endList.remove(address);
            endList.put(endAddress, recDest);
            beginList.remove(address);
            modified = true;
          }else
          { // error case - what to do???
            System.err.println("Error: unable to insert record!");
            System.exit(1);
          }
        }
      }
    }
  }
  public HexFileBase compare(HexFileBase other)
  {
    HexFileBase result = new HexFileBase();
    return result;
  }
  public int size()
  {
    return beginList.size();
  }
  public Iterator getIterator()
  {
    it = beginList.entrySet().iterator();
    return it;
  }
  public HexFileRecord get(int address)
  {
    return (HexFileRecord)beginList.get(address);
  }
  public void initIterator()
  {
    it = beginList.entrySet().iterator();
  }
  public HexFileRecord getFirst()
  {
    initIterator();
    return getNext();
  }
  public boolean hasNext()
  {
    return it.hasNext();
  }
  public HexFileRecord getNext()
  {
    if(it.hasNext())
    {
      Map.Entry me = (Map.Entry)it.next();
      return (HexFileRecord)me.getValue();
    }else
    {
      return null;
    }
  }
  public void load(String filename) throws HexFileException
  {
    throw new HexFileException("Invalid call of load of HexFileBase", "", "", -1);
  }
  //public ArrayList<HexFileRecord> data;
  TreeMap beginList;
  TreeMap endList;
  Iterator it;
}
