/*START HEADER*/
/* Zofar Survey System
* Copyright (C) 2014 Deutsches Zentrum für Hochschul- und Wissenschaftsforschung
* 
* This program is free software: you can redistribute it and/or modify
* it under the terms of the GNU General Public License as published by
* the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.
* 
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
* GNU General Public License for more details.
* 
* You should have received a copy of the GNU General Public License
* along with this program. If not, see <http://www.gnu.org/licenses/>.
*/
/*STOP HEADER*/
package eu.dzhw.zofar.management.utils.images.svg;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;
import eu.dzhw.zofar.management.utils.images.ImageConverter;
import java.util.TreeMap;
public class ImageTracer {
	/** The Constant INSTANCE. */
	private static final ImageTracer INSTANCE = new ImageTracer();
	private static String versionnumber = "1.1.2";
	public ImageTracer() {
		super();
	}
	public static synchronized ImageTracer getInstance() {
		return INSTANCE;
	}
	private class IndexedImage{
		public int width, height;
		public int [][] array; 
		public byte [][] palette;
		public ArrayList<ArrayList<ArrayList<Double[]>>> layers;
		public IndexedImage(int [][] marray, byte [][] mpalette){
			array = marray; palette = mpalette;
			width = marray[0].length-2; height = marray.length-2;
		}
	}
	// https://developer.mozilla.org/en-US/docs/Web/API/ImageData
	private class ImageData{
		public int width, height;
		public byte[] data; 
		public ImageData(int mwidth, int mheight, byte[] mdata){
			width = mwidth; height = mheight; data = mdata;
		}
	}
	private ImageData loadImageData (BufferedImage image) throws Exception {
		int width = image.getWidth(); int height = image.getHeight();
		int[] rawdata = image.getRGB(0, 0, width, height, null, 0, width);
		byte[] data = new byte[rawdata.length*4];
		for(int i=0; i<rawdata.length; i++){
			data[(i*4)+3] = bytetrans((byte)(rawdata[i] >>> 24));
			data[i*4  ] = bytetrans((byte)(rawdata[i] >>> 16));
			data[(i*4)+1] = bytetrans((byte)(rawdata[i] >>> 8));
			data[(i*4)+2] = bytetrans((byte)(rawdata[i]));
		}
		return new ImageData(width,height,data);
	}
	private byte bytetrans (byte b){
		if(b<0){ return (byte)(b+128); }else{ return (byte)(b-128); }
	}
	public String imageToSVG (BufferedImage image, HashMap<String,Float> options, byte [][] palette) throws Exception{
		options = checkoptions(options);
		ImageData imgd = loadImageData(image);
		return imagedataToSVG(imgd,options,palette);
	}
	private String imagedataToSVG (ImageData imgd, HashMap<String,Float> options, byte [][] palette){
		options = checkoptions(options);
		IndexedImage ii = imagedataToTracedata(imgd,options,palette);
		return getsvgstring(ii, options);
	}
	private IndexedImage imagedataToTracedata (ImageData imgd, HashMap<String,Float> options, byte [][] palette){
		IndexedImage ii = colorquantization(imgd, palette, options);
		int[][][] rawlayers = layering(ii);
		ArrayList<ArrayList<ArrayList<Integer[]>>> bps = batchpathscan(rawlayers,(int)(Math.floor(options.get("pathomit"))));
		ArrayList<ArrayList<ArrayList<Double[]>>> bis = batchinternodes(bps);
		ii.layers = batchtracelayers(bis,options.get("ltres"),options.get("qtres"));
		return ii;
	}
	private HashMap<String,Float> checkoptions (HashMap<String,Float> options){
		if(options==null){ options = new HashMap<String,Float>(); }
		if(!options.containsKey("ltres")){ options.put("ltres",1f); }
		if(!options.containsKey("qtres")){ options.put("qtres",1f); }
		if(!options.containsKey("pathomit")){ options.put("pathomit",8f); }
		if(!options.containsKey("colorsampling")){ options.put("colorsampling",1f); }
		if(!options.containsKey("numberofcolors")){ options.put("numberofcolors",16f); }
		if(!options.containsKey("mincolorratio")){ options.put("mincolorratio",0.02f); }
		if(!options.containsKey("colorquantcycles")){ options.put("colorquantcycles",3f); }
		if(!options.containsKey("scale")){ options.put("scale",1f); }
		if(!options.containsKey("simplifytolerance")){ options.put("simplifytolerance",0f); }
		if(!options.containsKey("roundcoords")){ options.put("roundcoords",1f); }
		if(!options.containsKey("lcpr")){ options.put("lcpr",0f); }
		if(!options.containsKey("qcpr")){ options.put("qcpr",0f); }
		if(!options.containsKey("desc")){ options.put("desc",1f); }
		if(!options.containsKey("viewbox")){ options.put("viewbox",0f); }
		if(!options.containsKey("blurradius")){ options.put("blurradius",0f); }
		if(!options.containsKey("blurdelta")){ options.put("blurdelta",20f); }
		return options;
	}
	// https://en.wikipedia.org/wiki/Color_quantization    https://en.wikipedia.org/wiki/K-means_clustering
	private IndexedImage colorquantization (ImageData imgd, byte [][] palette, HashMap<String,Float> options){
		int numberofcolors = (int)Math.floor(options.get("numberofcolors")); float minratio = options.get("mincolorratio"); int cycles = (int)Math.floor(options.get("colorquantcycles"));
		int [][] arr = new int[imgd.height+2][imgd.width+2];
		for(int j=0; j<(imgd.height+2); j++){ arr[j][0] = -1; arr[j][imgd.width+1 ] = -1; }
		for(int i=0; i<(imgd.width+2) ; i++){ arr[0][i] = -1; arr[imgd.height+1][i] = -1; }
		int idx=0, cd,cdl,ci,c1,c2,c3,c4;
		if(palette==null){
			if(options.get("colorsampling")!=0){
				palette = samplepalette(numberofcolors,imgd);
			}else{
				palette = generatepalette(numberofcolors);
			}
		}
		if( options.get("blurradius") > 0 ){ imgd = blur( imgd, options.get("blurradius"), options.get("blurdelta") ); }
		long [][] paletteacc = new long[palette.length][5];
		for(int cnt=0;cnt<cycles;cnt++){
			if(cnt>0){
				float ratio;
				for(int k=0;k<palette.length;k++){
					if(paletteacc[k][3]>0){
						palette[k][0] = (byte) (-128 + (paletteacc[k][0] / paletteacc[k][4]));
						palette[k][1] = (byte) (-128 + (paletteacc[k][1] / paletteacc[k][4]));
						palette[k][2] = (byte) (-128 + (paletteacc[k][2] / paletteacc[k][4]));
						palette[k][3] = (byte) (-128 + (paletteacc[k][3] / paletteacc[k][4]));
					}
					ratio = (float)( (double)(paletteacc[k][4]) / (double)(imgd.width*imgd.height) );
					if( (ratio<minratio) && (cnt<(cycles-1)) ){
						palette[k][0] = (byte) (-128+Math.floor(Math.random()*255));
						palette[k][1] = (byte) (-128+Math.floor(Math.random()*255));
						palette[k][2] = (byte) (-128+Math.floor(Math.random()*255));
						palette[k][3] = (byte) (-128+Math.floor(Math.random()*255));
					}
				}
			}
			for(int i=0;i<palette.length;i++){
				paletteacc[i][0]=0;
				paletteacc[i][1]=0;
				paletteacc[i][2]=0;
				paletteacc[i][3]=0;
				paletteacc[i][4]=0;
			}
			for(int j=0;j<imgd.height;j++){
				for(int i=0;i<imgd.width;i++){
					idx = ((j*imgd.width)+i)*4;
					cdl = 256+256+256+256; ci=0;
					for(int k=0;k<palette.length;k++){
						// In my experience, https://en.wikipedia.org/wiki/Rectilinear_distance works better than https://en.wikipedia.org/wiki/Euclidean_distance
						c1 = Math.abs(palette[k][0]-imgd.data[idx]);
						c2 = Math.abs(palette[k][1]-imgd.data[idx+1]);
						c3 = Math.abs(palette[k][2]-imgd.data[idx+2]);
						c4 = Math.abs(palette[k][3]-imgd.data[idx+3]);
						cd = c1+c2+c3+(c4*4); 
						if(cd<cdl){ cdl = cd; ci = k; }
					}
					paletteacc[ci][0] += 128+imgd.data[idx];
					paletteacc[ci][1] += 128+imgd.data[idx+1];
					paletteacc[ci][2] += 128+imgd.data[idx+2];
					paletteacc[ci][3] += 128+imgd.data[idx+3];
					paletteacc[ci][4]++;
					arr[j+1][i+1] = ci;
				}
			}
		}
		return new IndexedImage(arr, palette);
	}
	private byte[][] generatepalette (int numberofcolors){
		byte [][] palette = new byte[numberofcolors][4];
		if(numberofcolors<8){
			byte graystep = (byte) Math.floor(255/(numberofcolors-1));
			for(byte ccnt=0;ccnt<numberofcolors;ccnt++){
				palette[ccnt][0] = (byte)(-128+(ccnt*graystep));
				palette[ccnt][1] = (byte)(-128+(ccnt*graystep));
				palette[ccnt][2] = (byte)(-128+(ccnt*graystep));
				palette[ccnt][3] = (byte)255;
			}
		}else{
			int colorqnum = (int) Math.floor(Math.pow(numberofcolors, 1.0/3.0)); 
			int colorstep = (int) Math.floor(255/(colorqnum-1)); 
			int ccnt = 0;
			for(int rcnt=0;rcnt<colorqnum;rcnt++){
				for(int gcnt=0;gcnt<colorqnum;gcnt++){
					for(int bcnt=0;bcnt<colorqnum;bcnt++){
						palette[ccnt][0] = (byte)(-128+(rcnt*colorstep));
						palette[ccnt][1] = (byte)(-128+(gcnt*colorstep));
						palette[ccnt][2] = (byte)(-128+(bcnt*colorstep));
						palette[ccnt][3] = (byte)127;
						ccnt++;
					}
				}
			}
			for(int rcnt=ccnt;rcnt<numberofcolors;rcnt++){
				palette[ccnt][0] = (byte)(-128+Math.floor(Math.random()*255));
				palette[ccnt][1] = (byte)(-128+Math.floor(Math.random()*255));
				palette[ccnt][2] = (byte)(-128+Math.floor(Math.random()*255));
				palette[ccnt][3] = (byte)(-128+Math.floor(Math.random()*255));
			}
		}
		return palette;
	};
	private byte[][] samplepalette (int numberofcolors, ImageData imgd){
		int idx=0; byte [][] palette = new byte[numberofcolors][4];
		for(int i=0; i<numberofcolors; i++){
			idx = (int) (Math.floor( (Math.random() * imgd.data.length) / 4 ) * 4);
			palette[i][0] = imgd.data[idx  ];
			palette[i][1] = imgd.data[idx+1];
			palette[i][2] = imgd.data[idx+2];
			palette[i][3] = imgd.data[idx+3];
		}
		return palette;
	}
	private int[][][] layering (IndexedImage ii){
		int val=0, aw = ii.array[0].length, ah = ii.array.length, n1,n2,n3,n4,n5,n6,n7,n8;
		int[][][] layers = new int[ii.palette.length][ah][aw];
		for(int j=1; j<(ah-1); j++){
			for(int i=1; i<(aw-1); i++){
				val = ii.array[j][i];
				n1 = ii.array[j-1][i-1]==val ? 1 : 0;
				n2 = ii.array[j-1][i  ]==val ? 1 : 0;
				n3 = ii.array[j-1][i+1]==val ? 1 : 0;
				n4 = ii.array[j  ][i-1]==val ? 1 : 0;
				n5 = ii.array[j  ][i+1]==val ? 1 : 0;
				n6 = ii.array[j+1][i-1]==val ? 1 : 0;
				n7 = ii.array[j+1][i  ]==val ? 1 : 0;
				n8 = ii.array[j+1][i+1]==val ? 1 : 0;
				layers[val][j+1][i+1] = 1 + (n5 * 2) + (n8 * 4) + (n7 * 8) ;
				if(n4==0){ layers[val][j+1][i  ] = 0 + 2 + (n7 * 4) + (n6 * 8) ; }
				if(n2==0){ layers[val][j  ][i+1] = 0 + (n3*2) + (n5 * 4) + 8 ; }
				if(n1==0){ layers[val][j  ][i  ] = 0 + (n2*2) + 4 + (n4 * 8) ; }
			}
		}
		return layers;
	}
	private byte [] pathscan_dir_lookup = {0,0,3,0, 1,0,3,0, 0,3,3,1, 0,3,0,0};
	private boolean [] pathscan_holepath_lookup = {false,false,false,false, false,false,false,true, false,false,false,true, false,true,true,false };
	private byte [][][] pathscan_combined_lookup = {
			{{-1,-1,-1,-1}, {-1,-1,-1,-1}, {-1,-1,-1,-1}, {-1,-1,-1,-1}},
			{{ 0, 1, 0,-1}, {-1,-1,-1,-1}, {-1,-1,-1,-1}, { 0, 2,-1, 0}},
			{{-1,-1,-1,-1}, {-1,-1,-1,-1}, { 0, 1, 0,-1}, { 0, 0, 1, 0}},
			{{ 0, 0, 1, 0}, {-1,-1,-1,-1}, { 0, 2,-1, 0}, {-1,-1,-1,-1}},
			{{-1,-1,-1,-1}, { 0, 0, 1, 0}, { 0, 3, 0, 1}, {-1,-1,-1,-1}},
			{{13, 3, 0, 1}, {13, 2,-1, 0}, { 7, 1, 0,-1}, { 7, 0, 1, 0}},
			{{-1,-1,-1,-1}, { 0, 1, 0,-1}, {-1,-1,-1,-1}, { 0, 3, 0, 1}},
			{{ 0, 3, 0, 1}, { 0, 2,-1, 0}, {-1,-1,-1,-1}, {-1,-1,-1,-1}},
			{{ 0, 3, 0, 1}, { 0, 2,-1, 0}, {-1,-1,-1,-1}, {-1,-1,-1,-1}},
			{{-1,-1,-1,-1}, { 0, 1, 0,-1}, {-1,-1,-1,-1}, { 0, 3, 0, 1}},
			{{11, 1, 0,-1}, {14, 0, 1, 0}, {14, 3, 0, 1}, {11, 2,-1, 0}},
			{{-1,-1,-1,-1}, { 0, 0, 1, 0}, { 0, 3, 0, 1}, {-1,-1,-1,-1}},
			{{ 0, 0, 1, 0}, {-1,-1,-1,-1}, { 0, 2,-1, 0}, {-1,-1,-1,-1}},
			{{-1,-1,-1,-1}, {-1,-1,-1,-1}, { 0, 1, 0,-1}, { 0, 0, 1, 0}},
			{{ 0, 1, 0,-1}, {-1,-1,-1,-1}, {-1,-1,-1,-1}, { 0, 2,-1, 0}},
			{{-1,-1,-1,-1}, {-1,-1,-1,-1}, {-1,-1,-1,-1}, {-1,-1,-1,-1}}
	};
	private ArrayList<ArrayList<Integer[]>> pathscan (int [][] arr,float pathomit){
		ArrayList<ArrayList<Integer[]>> paths = new ArrayList<ArrayList<Integer[]>>();
		ArrayList<Integer[]> thispath;
		int px=0,py=0,w=arr[0].length,h=arr.length,dir=0;
		boolean pathfinished=true, holepath = false;
		byte[] lookuprow;
		for(int j=0;j<h;j++){
			for(int i=0;i<w;i++){
				if((arr[j][i]!=0)&&(arr[j][i]!=15)){
					px = i; py = j;
					paths.add(new ArrayList<Integer[]>());
					thispath = paths.get(paths.size()-1);
					pathfinished = false;
					dir = pathscan_dir_lookup[ arr[py][px] ]; holepath = pathscan_holepath_lookup[ arr[py][px] ];
					while(!pathfinished){
						thispath.add(new Integer[3]);
						thispath.get(thispath.size()-1)[0] = px-1;
						thispath.get(thispath.size()-1)[1] = py-1;
						thispath.get(thispath.size()-1)[2] = arr[py][px];
						lookuprow = pathscan_combined_lookup[ arr[py][px] ][ dir ];
						arr[py][px] = lookuprow[0]; dir = lookuprow[1]; px += lookuprow[2]; py += lookuprow[3];
						if(((px-1)==thispath.get(0)[0])&&((py-1)==thispath.get(0)[1])){
							pathfinished = true;
							if( (holepath) || (thispath.size()<pathomit) ){
								paths.remove(thispath);
							}
						}
					}
				}
			}
		}
		return paths;
	}
	private ArrayList<ArrayList<ArrayList<Integer[]>>> batchpathscan (int [][][] layers, float pathomit){
		ArrayList<ArrayList<ArrayList<Integer[]>>> bpaths = new ArrayList<ArrayList<ArrayList<Integer[]>>>();
		for (int[][] layer : layers) {
			bpaths.add(pathscan(layer,pathomit));
		}
		return bpaths;
	}
	private ArrayList<ArrayList<Double[]>> internodes (ArrayList<ArrayList<Integer[]>> paths){
		ArrayList<ArrayList<Double[]>> ins = new ArrayList<ArrayList<Double[]>>();
		ArrayList<Double[]> thisinp;
		Double[] thispoint, nextpoint = new Double[2];
		Integer[] pp1, pp2, pp3;
		int palen=0,nextidx=0,nextidx2=0;
		for(int pacnt=0; pacnt<paths.size(); pacnt++){
			ins.add(new ArrayList<Double[]>());
			thisinp = ins.get(ins.size()-1);
			palen = paths.get(pacnt).size();
			for(int pcnt=0;pcnt<palen;pcnt++){
				nextidx = (pcnt+1)%palen; nextidx2 = (pcnt+2)%palen;
				thisinp.add(new Double[3]);
				thispoint = thisinp.get(thisinp.size()-1);
				pp1 = paths.get(pacnt).get(pcnt);
				pp2 = paths.get(pacnt).get(nextidx);
				pp3 = paths.get(pacnt).get(nextidx2);
				thispoint[0] = (pp1[0]+pp2[0]) / 2.0;
				thispoint[1] = (pp1[1]+pp2[1]) / 2.0;
				nextpoint[0] = (pp2[0]+pp3[0]) / 2.0;
				nextpoint[1] = (pp2[1]+pp3[1]) / 2.0;
				if(thispoint[0] < nextpoint[0]){
					if     (thispoint[1] < nextpoint[1]){ thispoint[2] = 1.0; }
					else if(thispoint[1] > nextpoint[1]){ thispoint[2] = 7.0; }
					else                                { thispoint[2] = 0.0; } 
				}else if(thispoint[0] > nextpoint[0]){
					if     (thispoint[1] < nextpoint[1]){ thispoint[2] = 3.0; }
					else if(thispoint[1] > nextpoint[1]){ thispoint[2] = 5.0; }
					else                                { thispoint[2] = 4.0; }
				}else{
					if     (thispoint[1] < nextpoint[1]){ thispoint[2] = 2.0; }
					else if(thispoint[1] > nextpoint[1]){ thispoint[2] = 6.0; }
					else                                { thispoint[2] = 8.0; }
				}
			}
		}
		return ins;
	}
	private ArrayList<ArrayList<ArrayList<Double[]>>> batchinternodes (ArrayList<ArrayList<ArrayList<Integer[]>>> bpaths){
		ArrayList<ArrayList<ArrayList<Double[]>>> binternodes = new ArrayList<ArrayList<ArrayList<Double[]>>>();
		for(int k=0; k<bpaths.size(); k++) {
			binternodes.add(internodes(bpaths.get(k)));
		}
		return binternodes;
	}
	private ArrayList<Double[]> tracepath (ArrayList<Double[]> path, float ltreshold, float qtreshold){
		int pcnt=0, seqend=0; double segtype1, segtype2;
		ArrayList<Double[]> smp = new ArrayList<Double[]>();
		int pathlength = path.size();
		while(pcnt<pathlength){
			segtype1 = path.get(pcnt)[2]; segtype2 = -1; seqend=pcnt+1;
			while(
					((path.get(seqend)[2]==segtype1) || (path.get(seqend)[2]==segtype2) || (segtype2==-1))
					&& (seqend<(pathlength-1))){
				if((path.get(seqend)[2]!=segtype1) && (segtype2==-1)){ segtype2 = path.get(seqend)[2];}
				seqend++;
			}
			if(seqend==(pathlength-1)){ seqend = 0; }
			smp.addAll(fitseq(path,ltreshold,qtreshold,pcnt,seqend));
			if(seqend>0){ pcnt = seqend; }else{ pcnt = pathlength; }
		}
		return smp;
	}
	private ArrayList<Double[]> fitseq (ArrayList<Double[]> path, float ltreshold, float qtreshold, int seqstart, int seqend){
		ArrayList<Double[]> segment = new ArrayList<Double[]>();
		Double [] thissegment;
		int pathlength = path.size();
		if((seqend>pathlength)||(seqend<0)){return segment;}
		int errorpoint=seqstart;
		boolean curvepass=true;
		double px, py, dist2, errorval=0;
		double tl = (seqend-seqstart); if(tl<0){ tl += pathlength; }
		double vx = (path.get(seqend)[0]-path.get(seqstart)[0]) / tl,
				vy = (path.get(seqend)[1]-path.get(seqstart)[1]) / tl;
		int pcnt = (seqstart+1)%pathlength;
		double pl;
		while(pcnt != seqend){
			pl = pcnt-seqstart; if(pl<0){ pl += pathlength; }
			px = path.get(seqstart)[0] + (vx * pl); py = path.get(seqstart)[1] + (vy * pl);
			dist2 = ((path.get(pcnt)[0]-px)*(path.get(pcnt)[0]-px)) + ((path.get(pcnt)[1]-py)*(path.get(pcnt)[1]-py));
			if(dist2>ltreshold){curvepass=false;}
			if(dist2>errorval){ errorpoint=pcnt; errorval=dist2; }
			pcnt = (pcnt+1)%pathlength;
		}
		if(curvepass){
			segment.add(new Double[7]);
			thissegment = segment.get(segment.size()-1);
			thissegment[0] = 1.0;
			thissegment[1] = path.get(seqstart)[0];
			thissegment[2] = path.get(seqstart)[1];
			thissegment[3] = path.get(seqend)[0];
			thissegment[4] = path.get(seqend)[1];
			thissegment[5] = 0.0;
			thissegment[6] = 0.0;
			return segment;
		}
		int fitpoint = errorpoint; curvepass = true; errorval = 0;
		double t=(fitpoint-seqstart)/tl, t1=(1.0-t)*(1.0-t), t2=2.0*(1.0-t)*t, t3=t*t;
		double cpx = (((t1*path.get(seqstart)[0]) + (t3*path.get(seqend)[0])) - path.get(fitpoint)[0])/-t2 ,
				cpy = (((t1*path.get(seqstart)[1]) + (t3*path.get(seqend)[1])) - path.get(fitpoint)[1])/-t2 ;
		pcnt = seqstart+1;
		while(pcnt != seqend){
			t=(pcnt-seqstart)/tl; t1=(1.0-t)*(1.0-t); t2=2.0*(1.0-t)*t; t3=t*t;
			px = (t1 * path.get(seqstart)[0]) + (t2 * cpx) + (t3 * path.get(seqend)[0]);
			py = (t1 * path.get(seqstart)[1]) + (t2 * cpy) + (t3 * path.get(seqend)[1]);
			dist2 = ((path.get(pcnt)[0]-px)*(path.get(pcnt)[0]-px)) + ((path.get(pcnt)[1]-py)*(path.get(pcnt)[1]-py));
			if(dist2>qtreshold){curvepass=false;}
			if(dist2>errorval){ errorpoint=pcnt; errorval=dist2; }
			pcnt = (pcnt+1)%pathlength;
		}
		if(curvepass){
			segment.add(new Double[7]);
			thissegment = segment.get(segment.size()-1);
			thissegment[0] = 2.0;
			thissegment[1] = path.get(seqstart)[0];
			thissegment[2] = path.get(seqstart)[1];
			thissegment[3] = cpx;
			thissegment[4] = cpy;
			thissegment[5] = path.get(seqend)[0];
			thissegment[6] = path.get(seqend)[1];
			return segment;
		}
		int splitpoint = (fitpoint + errorpoint)/2;
		segment = fitseq(path,ltreshold,qtreshold,seqstart,splitpoint);
		segment.addAll(fitseq(path,ltreshold,qtreshold,splitpoint,seqend));
		return segment;
	}
	private ArrayList<ArrayList<Double[]>> batchtracepaths (ArrayList<ArrayList<Double[]>> internodepaths, float ltres,float qtres){
		ArrayList<ArrayList<Double[]>> btracedpaths = new ArrayList<ArrayList<Double[]>>();
		for(int k=0; k<internodepaths.size(); k++){
			btracedpaths.add(tracepath(internodepaths.get(k),ltres,qtres) );
		}
		return btracedpaths;
	}
	private ArrayList<ArrayList<ArrayList<Double[]>>> batchtracelayers (ArrayList<ArrayList<ArrayList<Double[]>>> binternodes, float ltres, float qtres){
		ArrayList<ArrayList<ArrayList<Double[]>>> btbis = new ArrayList<ArrayList<ArrayList<Double[]>>>();
		for(int k=0; k<binternodes.size(); k++){
			btbis.add( batchtracepaths( binternodes.get(k),ltres,qtres) );
		}
		return btbis;
	}
	private float roundtodec (float val, float places){
		return (float)(Math.round(val*Math.pow(10,places))/Math.pow(10,places));
	}
	private void svgpathstring (StringBuilder sb, String desc, ArrayList<Double[]> segments, String colorstr, HashMap<String,Float> options){
		float scale = options.get("scale"), lcpr = options.get("lcpr"), qcpr = options.get("qcpr"), roundcoords = (float) Math.floor(options.get("roundcoords"));
		sb.append("<path ").append(desc).append(colorstr).append("d=\"" ).append("M ").append(segments.get(0)[1]*scale).append(" ").append(segments.get(0)[2]*scale).append(" ");
		if( roundcoords == -1 ){
			for(int pcnt=0;pcnt<segments.size();pcnt++){
				if(segments.get(pcnt)[0]==1.0){
					sb.append("L ").append(segments.get(pcnt)[3]*scale).append(" ").append(segments.get(pcnt)[4]*scale).append(" ");
				}else{
					sb.append("Q ").append(segments.get(pcnt)[3]*scale).append(" ").append(segments.get(pcnt)[4]*scale).append(" ").append(segments.get(pcnt)[5]*scale).append(" ").append(segments.get(pcnt)[6]*scale).append(" ");
				}
			}
		}else{
			for(int pcnt=0;pcnt<segments.size();pcnt++){
				if(segments.get(pcnt)[0]==1.0){
					sb.append("L ").append(roundtodec((float)(segments.get(pcnt)[3]*scale),roundcoords)).append(" ")
					.append(roundtodec((float)(segments.get(pcnt)[4]*scale),roundcoords)).append(" ");
				}else{
					sb.append("Q ").append(roundtodec((float)(segments.get(pcnt)[3]*scale),roundcoords)).append(" ")
					.append(roundtodec((float)(segments.get(pcnt)[4]*scale),roundcoords)).append(" ")
					.append(roundtodec((float)(segments.get(pcnt)[5]*scale),roundcoords)).append(" ")
					.append(roundtodec((float)(segments.get(pcnt)[6]*scale),roundcoords)).append(" ");
				}
			}
		}
		sb.append("Z\" />");
		for(int pcnt=0;pcnt<segments.size();pcnt++){
			if((lcpr>0)&&(segments.get(pcnt)[0]==1.0)){
				sb.append( "<circle cx=\"").append(segments.get(pcnt)[3]*scale).append("\" cy=\"").append(segments.get(pcnt)[4]*scale).append("\" r=\"").append(lcpr).append("\" fill=\"white\" stroke-width=\"").append(lcpr*0.2).append("\" stroke=\"black\" />");
			}
			if((qcpr>0)&&(segments.get(pcnt)[0]==2.0)){
				sb.append( "<circle cx=\"").append(segments.get(pcnt)[3]*scale).append("\" cy=\"").append(segments.get(pcnt)[4]*scale).append("\" r=\"").append(qcpr).append("\" fill=\"cyan\" stroke-width=\"").append(qcpr*0.2).append("\" stroke=\"black\" />");
				sb.append( "<circle cx=\"").append(segments.get(pcnt)[5]*scale).append("\" cy=\"").append(segments.get(pcnt)[6]*scale).append("\" r=\"").append(qcpr).append("\" fill=\"white\" stroke-width=\"").append(qcpr*0.2).append("\" stroke=\"black\" />");
				sb.append( "<line x1=\"").append(segments.get(pcnt)[1]*scale).append("\" y1=\"").append(segments.get(pcnt)[2]*scale).append("\" x2=\"").append(segments.get(pcnt)[3]*scale).append("\" y2=\"").append(segments.get(pcnt)[4]*scale).append("\" stroke-width=\"").append(qcpr*0.2).append("\" stroke=\"cyan\" />");
				sb.append( "<line x1=\"").append(segments.get(pcnt)[3]*scale).append("\" y1=\"").append(segments.get(pcnt)[4]*scale).append("\" x2=\"").append(segments.get(pcnt)[5]*scale).append("\" y2=\"").append(segments.get(pcnt)[6]*scale).append("\" stroke-width=\"").append(qcpr*0.2).append("\" stroke=\"cyan\" />");
			}
		}
	}
	private String getsvgstring (IndexedImage ii, HashMap<String,Float> options){
		options = checkoptions(options);
		int w = (int) (ii.width * options.get("scale")), h = (int) (ii.height * options.get("scale"));
		String viewboxorviewport = options.get("viewbox")!=0 ? "viewBox=\"0 0 "+w+" "+h+"\" " : "width=\""+w+"\" height=\""+h+"\" ";
		StringBuilder svgstr = new StringBuilder("<svg "+viewboxorviewport+"version=\"1.1\" xmlns=\"http://www.w3.org/2000/svg\" ");
		if(options.get("desc")!=0){ svgstr.append("desc=\"Created with ImageTracer.java version "+ImageTracer.versionnumber+"\" "); }
		svgstr.append(">");
		TreeMap <Double,Integer[]> zindex = new TreeMap <Double,Integer[]>();
		double label;
		for(int k=0; k<ii.layers.size(); k++) {
			for(int pcnt=0; pcnt<ii.layers.get(k).size(); pcnt++){
				label = (ii.layers.get(k).get(pcnt).get(0)[2] * w) + ii.layers.get(k).get(pcnt).get(0)[1];
				if(!zindex.containsKey(label)){ zindex.put(label,new Integer[2]); }
				zindex.get(label)[0] = new Integer(k);
				zindex.get(label)[1] = new Integer(pcnt);
			}
		}
		String thisdesc = "";
		for(Entry<Double, Integer[]> entry : zindex.entrySet()) {
			if(options.get("desc")!=0){ thisdesc = "desc=\"l "+entry.getValue()[0]+" p "+entry.getValue()[1]+"\" "; }else{ thisdesc = ""; }
			svgpathstring(svgstr,
					thisdesc,
					ii.layers.get(entry.getValue()[0]).get(entry.getValue()[1]),
					tosvgcolorstr(ii.palette[entry.getValue()[0]]),
					options);
		}
		svgstr.append("</svg>");
		return svgstr.toString();
	}
	private String tosvgcolorstr (byte[] c){
		return "fill=\"rgb("+(c[0]+128)+","+(c[1]+128)+","+(c[2]+128)+")\" stroke=\"rgb("+(c[0]+128)+","+(c[1]+128)+","+(c[2]+128)+")\" stroke-width=\"1\" opacity=\""+((c[3]+128)/255.0)+"\" ";
	}
	private double[][] gks = { {0.27901,0.44198,0.27901}, {0.135336,0.228569,0.272192,0.228569,0.135336}, {0.086776,0.136394,0.178908,0.195843,0.178908,0.136394,0.086776},
			{0.063327,0.093095,0.122589,0.144599,0.152781,0.144599,0.122589,0.093095,0.063327}, {0.049692,0.069304,0.089767,0.107988,0.120651,0.125194,0.120651,0.107988,0.089767,0.069304,0.049692} };
	private ImageData blur (ImageData imgd, float rad, float del){
		int i,j,k,d,idx;
		double racc,gacc,bacc,aacc,wacc;
		ImageData imgd2 = new ImageData(imgd.width,imgd.height,new byte[imgd.width*imgd.height*4]);
		int radius = (int)Math.floor(rad); if(radius<1){ return imgd; } if(radius>5){ radius = 5; }
		int delta = (int)Math.abs(del); if(delta>1024){ delta = 1024; }
		double[] thisgk = gks[radius-1];
		for( j=0; j < imgd.height; j++ ){
			for( i=0; i < imgd.width; i++ ){
				racc = 0; gacc = 0; bacc = 0; aacc = 0; wacc = 0;
				for( k = -radius; k < (radius+1); k++){
					if( ((i+k) > 0) && ((i+k) < imgd.width) ){
						idx = ((j*imgd.width)+i+k)*4;
						racc += imgd.data[idx  ] * thisgk[k+radius];
						gacc += imgd.data[idx+1] * thisgk[k+radius];
						bacc += imgd.data[idx+2] * thisgk[k+radius];
						aacc += imgd.data[idx+3] * thisgk[k+radius];
						wacc += thisgk[k+radius];
					}
				}
				idx = ((j*imgd.width)+i)*4;
				imgd2.data[idx  ] = (byte) Math.floor(racc / wacc);
				imgd2.data[idx+1] = (byte) Math.floor(gacc / wacc);
				imgd2.data[idx+2] = (byte) Math.floor(bacc / wacc);
				imgd2.data[idx+3] = (byte) Math.floor(aacc / wacc);
			}
		}
		byte[] himgd = imgd2.data.clone();
		for( j=0; j < imgd.height; j++ ){
			for( i=0; i < imgd.width; i++ ){
				racc = 0; gacc = 0; bacc = 0; aacc = 0; wacc = 0;
				for( k = -radius; k < (radius+1); k++){
					if( ((j+k) > 0) && ((j+k) < imgd.height) ){
						idx = (((j+k)*imgd.width)+i)*4;
						racc += himgd[idx  ] * thisgk[k+radius];
						gacc += himgd[idx+1] * thisgk[k+radius];
						bacc += himgd[idx+2] * thisgk[k+radius];
						aacc += himgd[idx+3] * thisgk[k+radius];
						wacc += thisgk[k+radius];
					}
				}
				idx = ((j*imgd.width)+i)*4;
				imgd2.data[idx  ] = (byte) Math.floor(racc / wacc);
				imgd2.data[idx+1] = (byte) Math.floor(gacc / wacc);
				imgd2.data[idx+2] = (byte) Math.floor(bacc / wacc);
				imgd2.data[idx+3] = (byte) Math.floor(aacc / wacc);
			}
		}
		for( j=0; j < imgd.height; j++ ){
			for( i=0; i < imgd.width; i++ ){
				idx = ((j*imgd.width)+i)*4;
				d = Math.abs(imgd2.data[idx  ] - imgd.data[idx  ]) + Math.abs(imgd2.data[idx+1] - imgd.data[idx+1]) +
						Math.abs(imgd2.data[idx+2] - imgd.data[idx+2]) + Math.abs(imgd2.data[idx+3] - imgd.data[idx+3]);
				if(d>delta){
					imgd2.data[idx  ] = imgd.data[idx  ];
					imgd2.data[idx+1] = imgd.data[idx+1];
					imgd2.data[idx+2] = imgd.data[idx+2];
					imgd2.data[idx+3] = imgd.data[idx+3];
				}
			}
		}
		return imgd2;
	}
}
