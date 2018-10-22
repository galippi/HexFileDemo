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
public class HexBlockHeader implements Comparable {
  public int begin;
  public int end;
  public int len;
  public HexBlockHeader(int address, int len)
  {
    begin = address;
    this.len = len;
    end = begin + len;
  }
  @Override
    public int compareTo(Object other)
  {
    //ascending order
    return this.begin - ((HexBlockHeader)other).begin;
  }  
}
