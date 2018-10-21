/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hexfiledemo.HexFile;

/**
 *
 * @author liptakok
 */
public class HexFileRecord {
  public HexFileRecord(int address, byte[] data)
  {
    this.address = address;
    this.data = new byte[data.length];
    java.lang.System.arraycopy(data, 0, this.data, 0, data.length);
    end = this.address + data.length;
  }
  public boolean insert(int address, byte[] data)
  {
    if (end == address)
    {
      byte[] dataNew = new byte[this.data.length + data.length];
      java.lang.System.arraycopy(this.data, 0, dataNew,                0, this.data.length);
      java.lang.System.arraycopy(     data, 0, dataNew, this.data.length, data.length);
      this.data = dataNew;
      end += data.length;
      return true;
    }else
    if ((address + data.length) == this.address)
    {
      byte[] dataNew = new byte[this.data.length + data.length];
      java.lang.System.arraycopy(     data, 0, dataNew,           0, data.length);
      java.lang.System.arraycopy(this.data, 0, dataNew, data.length, this.data.length);
      this.data = dataNew;
      address = address - data.length;
      return true;
    }else
    {
      return false;
    }
  }
  public int getAddress()
  {
    return address;
  }
  public int size()
  {
    return data.length;
  }
  public byte[] getData()
  {
    return data;
  }
  int address;
  int end;
  byte[] data;
}
