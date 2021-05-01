package CMPRTest;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

import javax.imageio.ImageIO;

public class main {
	
 static File f;
 static File p;
 static RandomAccessFile r;


 
 	final static  short[] cc38 = // convert 3-bit color to 8-bit color
 {
     0x00,0x24,0x49,0x6d, 0x92,0xb6,0xdb,0xff
 };

 	final static  short[] cc48 = // convert 4-bit color to 8-bit color
 {
     0x00,0x11,0x22,0x33, 0x44,0x55,0x66,0x77, 0x88,0x99,0xaa,0xbb, 0xcc,0xdd,0xee,0xff
 };
 
	final static short[] cc58 =
	{
	    0x00,0x08,0x10,0x19, 0x21,0x29,0x31,0x3a, 0x42,0x4a,0x52,0x5a, 0x63,0x6b,0x73,0x7b,
	    0x84,0x8c,0x94,0x9c, 0xa5,0xad,0xb5,0xbd, 0xc5,0xce,0xd6,0xde, 0xe6,0xef,0xf7,0xff
	};
	
	final static short [] cc68 = // convert 6-bit color to 8-bit color
		{
		    0x00,0x04,0x08,0x0c, 0x10,0x14,0x18,0x1c, 0x20,0x24,0x28,0x2d, 0x31,0x35,0x39,0x3d,
		    0x41,0x45,0x49,0x4d, 0x51,0x55,0x59,0x5d, 0x61,0x65,0x69,0x6d, 0x71,0x75,0x79,0x7d,
		    0x82,0x86,0x8a,0x8e, 0x92,0x96,0x9a,0x9e, 0xa2,0xa6,0xaa,0xae, 0xb2,0xb6,0xba,0xbe,
		    0xc2,0xc6,0xca,0xce, 0xd2,0xd7,0xdb,0xdf, 0xe3,0xe7,0xeb,0xef, 0xf3,0xf7,0xfb,0xff
		};
 
 public static void main(String[] args) throws IOException {
	 
	 int w = 0;
	 int h = 0;
	 boolean foundWh =false;
	 boolean wd = false;

	 if(args.length == 0) {
		 System.out.println("Usage: ");
	 	 System.out.println("extract: java -jar sgct.jar -e path_to_file.gct [-wh width_as_integer height_as_integer] [-pos pos_in_hex] \n");
	 	 System.out.println("inject : java -jar sgct.jar -i path_to_file.png -to path_to_file.gct [-pos pos_in_hex] -wd (<- this means to write the image dimensions to 0x10, the standard location) ");
	 	 System.exit(0);
	 	 
	 }
	 
	 	f = new File(args[1]);
	 	r = new RandomAccessFile(f, "rw");
	 	
 		//get position to read from/write to
 		long pos = 0x40;
 		
 		for(int i = 0; i < args.length; ++i) 
 			if(args[i].equals("-pos")) {
 				pos=Integer.parseInt(args[i+1],16);
 				r.seek(pos);
 				
 			}
 		
 		System.out.println("\n");
	 
	 	switch(args[0]) {
	 	
	 	//dims: 0x10
	 	//start: 0x40

		 	case "-e":
		 		p = new File(f.getParent()+"\\"+sE(f.getName())+".png");
		 		
		 		for(int i = 0; i < args.length; ++i) {
		 			
		 			if(args[i].equals("-wh")) {
		 			
		 			//get w and h from cmdline


			 			w = Integer.parseInt(args[i+1]);
			 			h = Integer.parseInt(args[i+2]);
			 			foundWh = true;
			 			break;
			 		
		 			}
			 		}
		 		
	 			
		 		if(!foundWh) {
		 		r.seek(0x10);
		 		w = (int)r.readShort() & 0xFFFF;
		 		h = (int)r.readShort() & 0xFFFF;
		 		}
		 		
		 		
		 		if(w == 0 || h == 0)
		 			w=h=4;
		 		
		 		r.seek(pos); //return to pos
 		 
		 String stuff = String.format("extracting image from %s of size %dx%d from position %x\n",f.getName(),w,h,pos);
		 
		 System.out.println(stuff);
 		 
 		
 		BufferedImage pn = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);	
 		
 		int X = 0;
 		int Y = 0;
 		int dx = 0;
 		int dy = 0;
 		
 		int dyHack = 0;
 		
 	while(Y<h-4) {
 			
 		
 		
 		if(dx>=8) {
 			
 			if(dyHack == X)
 				dy=4;
 			else dy = 0;
 			
 			dx=0;
 			
 		}
 		rwblock(X+dx,Y+dy,pn);
 		
 		dyHack = X;
 		if(dx == 4 && dy == 4)  { X+=8;}
 		
 		
 		dx+=4;
 		
 		if(X>w-4) {
 			X= 0 ;
 			Y+=8;
 		}
 		
 	}
 		ImageIO.write(pn, "png", p);
 		System.out.println("done.");
 		break;
 		
		 	case "-i":
		 		File o =new File(args[3]);
		 		
		 		for(int i = 0; i < args.length; ++i) 
		 			if(args[i].equals("-wd")) 
		 				wd=true;
		 		
		 		String stuff1;
		 		
		 		if(wd)
		 			stuff1 = String.format("injecting image %s into %s at position 0x%x and updating dimensions at 0x10\n",f.getName(),o.getName(),pos);
		 		else
		 			stuff1 = String.format("injecting image %s into %s at position 0x%x\n",f.getName(),o.getName(),pos);
		 		
				System.out.println(stuff1);
		 		
		 		encTest.begin(f, o,pos,wd);
		 		System.out.println("done.");
		 		break;
		 	
		 		default:
		 			System.out.println("lolwut");
		 		
		 		
 	}
 		
 }
 
 
 public static void rwblock(int X, int Y, BufferedImage pn) throws IOException {
	 
	 	int c0 = r.readShort()&0xFFFF;
		int c1 = r.readShort()&0xFFFF;
		
		int c2 = 0;
		int c3 = 0;
		
		if(c0>=c1) {
		
		 c2 = mixColors(c0, c1, 2, 1, 3);
		 c3 = mixColors(c0, c1, 1, 2, 3);
		
		} else {
			
			 c2 = mixColors(c0, c1, 1, 1, 2);
			 c3 = 0;
			
		}
		

		c0 = c565to888(c0);
		c1 = c565to888(c1);
		c2 = c565to888(c2);
		c3 = c565to888(c3);
		
		int[] colors = new int[4];
		
		colors[0] = c0;
		colors[1] = c1;
		colors[2] = c2;
		colors[3] = c3;
		
	//	System.out.println("X: "+Integer.toHexString(colors[0])+" Y: "+Integer.toHexString(colors[1])+" dx: "+Integer.toHexString(colors[2])+" dy: "+Integer.toHexString(colors[3]));
		
		int[] block = getBlock();


		writeBlock(block, colors, X, Y, pn);
	 
 }
 
 public static int[] getBlock() throws IOException{
 		
 		int[] blocc = new int[16];
 		int ind = r.readInt();
 		int index;		
 		
 		//System.out.println(Integer.toBinaryString(ind));
 		
 		int k = 0;
 		int j = 0;
 		
	 		for(int i = 0 ; i <16; ++i) 
	 			 blocc[i] = (ind >> 30-(2*i)) & 0b11; //get each 2 bit index
 		
		return blocc;
 		
 		
 		}
 
private static void writeBlock(int[] block, int[] c,int x, int y, BufferedImage p) {
	
	int index = 0;
		
    for(int dy = 0; dy < 4; dy++) 
        for(int dx = 0; dx < 4; dx++) {
			p.setRGB(x+dx, y+dy, c[block[index++]]);

        }
	
	
}


 			
 
private static int mixColors(int color0, int color1, int mul1, int mul2, int div) {
    int r0 = (color0 >>> 11) & 31;
    int g0 = (color0 >>> 5) & 63;
    int b0 = color0 & 31;
    int r1 = (color1 >>> 11) & 31;
    int g1 = (color1 >>> 5) & 63;
    int b1 = color1 & 31;
    int r = (r0 * mul1 + r1 * mul2) / div;
    int g = (g0 * mul1 + g1 * mul2) / div;
    int b = (b0 * mul1 + b1 * mul2) / div;
    return (Math.round(r) << 11) | (Math.round(g) << 5) | (Math.round(b));
}
	
 
 public static int c565to888(int c) {
	
		
	 int ret = 0xFF<<24 | cc58[ c >> 11        ] << 16	// red
			 | cc68[ c >>  5 & 0x3f ] <<  8	// green
			 | cc58[ c       & 0x1f ];		// blue


	 return ret;
	
 }
 
 private static void write5A3Block( int x, int y, BufferedImage pn) throws IOException {
		

	 for(int dy = 0; dy < 4; dy++) {
         for(int dx = 0; dx < 4; dx++) {
        	 
        	 	int color = r.readShort()&0xFFFF;
        	 	
        	 	System.out.println(Integer.toBinaryString(color));
        	 	
        
        		
            
            
                pn.setRGB(x + dx, y + dy, (((color >> 15) & 0x1) == 0x1 ? RGBto888(color, false) : RGBto888(color, true) ));
         }
     }
	
}
 
 public static int RGBto888(int color, boolean alpha) {
	 
	 int ret  = 0;
	 
	 if(alpha)
		 ret = cc38[(color>>12) &7] <<24 | cc48[((color >> 8) & 0xF)] <<16 |  cc48[((color >> 4) & 0xF)] << 8 | cc48[color & 0xF];
	 else 
		 ret = (0xFF<<24) | (cc58[((color >> 10) & 0x1F)] <<16) |  (cc58[((color >> 5) & 0x1F)] << 8) | cc58[color & 0x1F];
	 
	 pint(ret);
	return ret;
	 
	 
 }
 
 static void hashGen(String h) {
	 
	 
	 int number = 0;
	 
	 
	 for(int i = 0; i < h.length(); ++i)
		 number += h.charAt(i);
	 

	 
	number = number*64 | number<<24;
	 
	 pint(number);
 }
 
 public static void decRGB5A3(int w, int h, BufferedImage pn) throws IOException  {
	 
		int x = 0;
 		int y = 0;
 		int dx = 0;
 		int dy = 0;
 		
 		int dyHack = 0;
 		
 	while(y<h) {


 		
 		write5A3Block(x,y,pn);
 	 	//System.out.println("X: "+X+" Y: "+Y+" dx: "+dx+" dy: "+dy);

	 	
 		x+= 4;
	    if(x >= w) {
            x = 0;
            y += 4;
        
    }
 		
 		
 		
 		}
 		
			// 1RRRRRGGGGGBBBBB
         
 
	 
	 System.out.println("done.");
	 ImageIO.write(pn, "png", p);
	 System.exit(0);
 
 }
 
 public static void decRGBA8(int w, int h, BufferedImage pn) throws IOException  {
	 
		int x = 0;
		int y = 0;
		int dx = 0;
		int dy = 0;
		
		int dyHack = 0;
		
	while(y<h) {


		
		writeRGB8Block(x,y,pn);
	 	//System.out.println("X: "+X+" Y: "+Y+" dx: "+dx+" dy: "+dy);

	 	
		x+= 4;
	    if(x >= w) {
         x = 0;
         y += 4;
     
 }
		
		
		
		}
		
      

	 
	 System.out.println("done.");
	 ImageIO.write(pn, "png", p);
	 System.exit(0);

}
 
 private static void writeRGB8Block( int x, int y, BufferedImage pn) throws IOException {
	 
	 int[] AR = new int[16];
	 int[] GB = new int[16];
	 int[] colors = new int[16];
	 int ind = 0;
	 
	 for(int c = 0; c <16; ++c) 
		AR[c] = r.readShort();
	 
	 for(int c = 0; c <16; ++c) 
		GB[c] = r.readShort();
	 
	 for(int c = 0; c <16; ++c) {
		colors[c] = (AR[c]<<16) | GB[c]&0xFFFF;
	 
	 pint(colors[c]);
	
	 }

	 
	 
	 for(int dy = 0; dy < 4; dy++) {
         for(int dx = 0; dx < 4; dx++) {

                pn.setRGB(x + dx, y + dy, colors[ind++]);
         }
         }
     }
 			
	public static void pint(int txt) {
		
		System.out.println(Integer.toHexString(txt)+" ");
	}

	public static void pinft(long txt) {
		
		System.out.println(Long.toHexString(txt)+" ");
	}
	
	
    static String sE (String str) {
        // Handle null case specially.

        if (str == null) return null;

        // Get position of last '.'.

        int pos = str.lastIndexOf(".");

        // If there wasn't any '.' just return the string as is.

        if (pos == -1) return str;

        // Otherwise return the string, up to the dot.

        return str.substring(0, pos);
    }
	
 		
 		
 		
		
	}


