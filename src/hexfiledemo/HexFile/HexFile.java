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
    for (int i = 0; i < file.size(); i++)
    {
      HexFileRecord rec = file.get(i);
      InsertRecord(rec.address, rec.getData());
    }
  }
}
