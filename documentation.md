#Disk
## Members
- Block size: 5B
- Size: 20MB = 20_000_000B
- Number of blocks: 4_000_000
- readCount - every time a file is read increment
- writeCount - every time a file is written increment

## Methods
- read
    - read single block to a buffer
    - read SuperBlock
    - read InodeBlock
    - read Block
- write
    - write SuperBlock
    - write InodeBlock
    - write buffer
    
## Notes
- **char** is two bytes (when writing names of files and dirs)
- **Integer** is 16 bytes
- first inode is the inode of the root dir
- first currentDir is the root dir
- InodeBlock is in the memmory, on shutdown must write it manually in header section (list is dynamics)
- **Format a date:** System.out.println(new SimpleDateFormat("HH:mm:ss").format(a));
- **extent size** - the last block is noninclusive