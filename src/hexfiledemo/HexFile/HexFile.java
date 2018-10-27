/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hexfiledemo.HexFile;

import java.util.Iterator;
import java.util.Map;

/**
 *
 * @author liptakok
 */
public class HexFile extends HexFileBase {
  public HexFile()
  {
    super();
  }
  public HexFile(String filename) throws HexFileException
  {
    super();
    load(filename);
  }
  public void load(String filename) throws HexFileException
  {
    HexFileBase file;
    try {
      file = new MotoHexFile(filename);
    }catch (HexFileException em) {
      try {
        file = new IntelHexFile(filename);
      }catch (HexFileException ei) {
        System.out.println("Error: " + em.toString());
        System.out.println("Error: " + ei.toString());
        throw new HexFileException("Unable to load file", filename, "", -1);
      }
    }
    Iterator i = file.beginList.entrySet().iterator();
    while(i.hasNext()) {
      Map.Entry me = (Map.Entry)i.next();
      HexFileRecord rec = (HexFileRecord)me.getValue();
      InsertRecord(rec);
    }
  }
}
