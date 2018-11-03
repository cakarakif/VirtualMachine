package pack;

public class FileNode {
	public String filename;
	public int diskstart;
	public int size;
	public FileNode nextName;
	public FileNode nextDiskstart;
	public Node rightLinked;

	public FileNode(String filename,int diskstart) {
		size = 0;
		this.filename=filename;
		this.diskstart=diskstart;
		nextName = null;
		nextDiskstart=null;
		rightLinked = null;
	}
}
