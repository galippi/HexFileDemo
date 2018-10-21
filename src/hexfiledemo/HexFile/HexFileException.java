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
public class HexFileException extends java.lang.Exception {
  public HexFileException(String Error, String filename, String line, int lineIdx)
  {
    this.Error = Error;
    this.filename = filename;
    this.line = line;
    this.lineIdx = lineIdx;
  }
  @Override public String toString()
  {
    String str = Error + " file=" + filename + " line=" + line + " in line " + lineIdx + "!";
    return str;
  }
  public String Error;
  public String filename;
  public String line;
  public int lineIdx;
}
