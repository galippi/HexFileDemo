/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hexfiledemo.HexFile;

import java.util.ArrayList;
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
  public void InsertRecord(HexFileRecord rec) throws HexFileException
  {
    InsertRecord(rec.address, rec.getData());
  }
  public void InsertRecord(int address, byte[] data) throws HexFileException
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
      if (beginList.containsKey(address))
        throw new HexFileException("Overlapping hex file address=" + Integer.toHexString(address), null, null, -1);
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
  void pack() throws HexFileException
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
    // checking overlapping blocks
    initIterator();
    HexFileRecord rec0 = getNext();
    if (rec0 == null)
      throw new HexFileException("Empty hex file", null, null, -1);
    HexFileRecord rec1;
    while((rec1 = getNext()) != null) {
      if (rec0.end >= rec1.address)
        throw new HexFileException("Overlapping hex file address=" + Integer.toHexString(rec1.address), null, null, -1);
    }
  }
  public HexBlockHeader[] compare(HexFileBase other)
  {
    ArrayList<HexBlockHeader> result = new ArrayList<>();
    HexFileRecord recThis = this.getFirst();
    HexFileRecord recOther = other.getFirst();
    int address;
    if (recThis.address < recOther.address)
      address = recThis.address;
    else
      address = recOther.address;
    while((recThis != null) && (recOther != null))
    {
      if ((recThis.isIn(address)) && (recOther.isIn(address)))
      {
        int end = recThis.end;
        if (end > recOther.end)
          end = recOther.end;
        int idxThis = recThis.address - address;
        int idxOther = recOther.address - address;
        byte[] dataThis = recThis.getData();
        byte[] dataOther = recOther.getData();
        int diffLen = 0;
        int diffAddress = 0;
        for(int j = address; j < end; j++)
        {
          if (dataThis[idxThis] != dataOther[idxOther])
          {
            if (diffLen == 0)
            {
              diffAddress = j;
            }
            diffLen++;
          }else
          if (diffLen != 0)
          { // again same data - store block
            HexBlockHeader hdr = new HexBlockHeader(diffAddress, diffLen);
            result.add(hdr);
            diffLen = 0;
          }
          idxThis++; idxOther++;
        }
        if (diffLen != 0)
        { // again same data - store block
          HexBlockHeader hdr = new HexBlockHeader(diffAddress, diffLen);
          result.add(hdr);
        }
        address = end;
      }else
      if (recThis.isIn(address))
      {
        int end = recThis.end;
        if (end >= recOther.address)
          end = recOther.address;
        HexBlockHeader hdr = new HexBlockHeader(address, end - address);
        result.add(hdr);
        address = end;
      }else
      if (recOther.isIn(address))
      {
        int end = recOther.end;
        if (end >= recThis.address)
          end = recThis.address;
        HexBlockHeader hdr = new HexBlockHeader(address, end - address);
        result.add(hdr);
        address = end;
      }else
      {
        if (address >= recThis.end)
        {
          recThis = this.getNext();
        }
        if (address >= recOther.end)
        {
          recOther = other.getNext();
        }
        if ((recThis != null) && (recOther != null))
        {
          if (recThis.address < recOther.address)
          {
            if (recThis.address <= address)
            {
              if (recOther.address <= address)
              {
              }else
              {
                address = recOther.address;
              }
            }else
            {
              address = recThis.address;
            }
          }else
          {
            if (recOther.address <= address)
            {
              address = recThis.address;
            }else
            {
              address = recOther.address;
            }
          }
        }
      }
    }
    HexFileBase ptr = this;
    if (recOther != null)
    {
      recThis = recOther; /* recThis was surely null */
      ptr = other;
    }
    if (recThis != null)
    {
      if (address < recThis.address)
        address = recThis.address;
      if (address < recThis.end)
      {
        HexBlockHeader hdr = new HexBlockHeader(address, recThis.end - address);
        result.add(hdr);
      }
      while((recThis = ptr.getNext()) != null)
      {
        HexBlockHeader hdr = new HexBlockHeader(recThis.address, recThis.size());
        result.add(hdr);
      }
    }
    if (result.isEmpty())
      return null;
    HexBlockHeader[] result2 = new HexBlockHeader[result.size()];
    for (int i = 0; i < result2.length; i++)
      result2[i] = result.get(i);
    return result2;
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
    if (it == null)
      return false;
    if (it.hasNext())
      return true;
    it = null;
    return false;
  }
  public HexFileRecord getNext()
  {
    if (it == null)
    {
      initIterator();
    }
    if(it.hasNext())
    {
      Map.Entry me = (Map.Entry)it.next();
      return (HexFileRecord)me.getValue();
    }else
    {
      it = null;
      return null;
    }
  }
  public void load(String filename) throws HexFileException
  {
    throw new HexFileException("Invalid call of load of HexFileBase", "", "", -1);
  }
  public void SwapU32() throws HexFileException
  {
    initIterator();
    HexFileRecord rec;
    while((rec = getNext()) != null) {
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
  }
  public byte[] GeData(int address, int len) throws HexFileException
  {
    Map.Entry<Integer, Object> entry = beginList.floorEntry(address);
    HexFileRecord rec = null;
    if (entry != null)
      rec = (HexFileRecord)entry.getValue();
    if ((rec == null) || (rec.end < (address + len)))
      throw new HexFileException("No data in the given range (" + Integer.toHexString(address) + ", " + len + ")", "", "", -1);
    byte[] result = new byte[len];
    java.lang.System.arraycopy(rec.getData(), address - rec.address, result, 0, len);
    return result;
  }
  TreeMap beginList;
  TreeMap endList;
  Iterator it;
}
