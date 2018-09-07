package icfp.endo;

/***********************************************************************
 * Interface for a block of DNA.
 * 
 * Created an interface in case it becomes necessary to not read the files into
 * memory. The thinking is there could be two implementations of this interface,
 * one for Strings and one for Files.
 ***********************************************************************/
public interface DNAChunk {
  int size();

  void setStart(int start);

  void setEnd(int end);

  void trunc(int size);

  char get(int ndx);

  char pop();

  String substring(int ndx);

  String substring(int start, int end);

  DNAChunk copy(int start, int end);
}
