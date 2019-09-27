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
