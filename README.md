# sgct

Tool for extracting and injecting the .gct texture files that ship with the GameCube version of the game.

## Usage

```sh
extract: java -jar sgct.jar -e path_to_file.gct [-wh width_as_integer height_as_integer] [-pos pos_in_hex]

inject : java -jar sgct.jar -i path_to_file.png -to path_to_file.gct [-pos pos_in_hex] -wd (<- this means to write the image dimensions to 0x10, the standard location)
```