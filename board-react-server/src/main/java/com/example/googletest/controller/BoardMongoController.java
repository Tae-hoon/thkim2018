package com.example.googletest.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
public class BoardMongoController {
	
	@Autowired
	private Environment env;
	
	@Autowired
    private MongoTemplate mongoTemplate;
	
	@Autowired
	private BoardRepository boardRepository;
	

	@RequestMapping("/board")
    public String board() throws Exception {
		System.out.println("board=====");		
        return "board";
    }
	
	@RequestMapping("/board2")
    public String board2() throws Exception {
		System.out.println("board2=====");		
        return "board2";
    }
	
	@RequestMapping("/list")
	@ResponseBody
    public Map<String, Object> list() throws Exception {
		System.out.println("BoardMongoController list=====");		
		Map<String, Object> map = new HashMap<>();
		List<Board> list = new ArrayList<Board>();
		
		list = boardRepository.findAll();
		
        map.put("list", list);
        
        return map;
    }
	
	@RequestMapping(value="/add", method=RequestMethod.POST)
	@ResponseBody
    public Map<String, Object> add(@RequestParam(value="title", required=true) String title,
			@RequestParam(value="contents", required=false, defaultValue="") String contents,
			@RequestParam(value="file", required=false) MultipartFile file) throws Exception {
		System.out.println("add=====");		
		Map<String, Object> map = new HashMap<>();
		
		SimpleDateFormat format1 = new SimpleDateFormat ("yyyy-MM-dd hh:MM");				
		Date time = new Date();				
		String ymd = format1.format(time);

		try{
			String repository = env.getProperty("user.file.upload");
			String fname = "";
			if( file != null && file.getSize() > 0 ) {
				fname = file.getOriginalFilename();
				FileOutputStream fos = new FileOutputStream(new File(repository+File.separator+fname));
				IOUtils.copy(file.getInputStream(), fos);
				fos.close();
			}
			
			Board in = new Board();
			in.setTitle(title);
			in.setContents(contents);
			in.setFname(fname);
			in.setDate(ymd);
			boardRepository.insert(in);
			
			map.put("returnCode", "success");
			map.put("returnDesc", "");
		}catch(Exception e){
			map.put("returnCode", "failed");
			map.put("returnDesc", "데이터 등록에 실패하였습니다.");
		}
		        
        return map;
    }
	
	@RequestMapping(value="/add2", method=RequestMethod.POST)
	@ResponseBody
    public Map<String, Object> add2(@RequestParam(value="title", required=true) String title,
			@RequestParam(value="contents", required=false, defaultValue="") String contents,
			@RequestParam(value="file", required=false) List<MultipartFile> files) throws Exception {
		System.out.println("add2=====");		
		Map<String, Object> map = new HashMap<>();
		
		SimpleDateFormat format1 = new SimpleDateFormat ("yyyy-MM-dd hh:MM");				
		Date time = new Date();				
		String ymd = format1.format(time);

		try{
			String repository = env.getProperty("user.file.upload");
			String fnames = "";
			for(MultipartFile file : files){
				if( file != null && file.getSize() > 0 ) {
					String fname = file.getOriginalFilename();
					System.out.println("fname:"+fname);
					FileOutputStream fos = new FileOutputStream(new File(repository+File.separator+fname));
					IOUtils.copy(file.getInputStream(), fos);
					fos.close();
					
					if( "".equals(fnames) ){
						fnames = fname;
					}else{
						fnames += ","+fname;
					}
				}
			}
			
			Board in = new Board();
			in.setTitle(title);
			in.setContents(contents);
			in.setFname(fnames);
			in.setDate(ymd);
			boardRepository.insert(in);
			
			map.put("returnCode", "success");
			map.put("returnDesc", "");
		}catch(Exception e){
			map.put("returnCode", "failed");
			map.put("returnDesc", "데이터 등록에 실패하였습니다.");
		}
		        
        return map;
    }
	
	@RequestMapping(value="/mod", method=RequestMethod.POST)
	@ResponseBody
    public Map<String, Object> mod(@RequestParam(value="id", required=true) String id,
    		@RequestParam(value="title", required=true) String title,
			@RequestParam(value="contents", required=false, defaultValue="") String contents,
			@RequestParam(value="file", required=false) MultipartFile file) throws Exception {
		System.out.println("mod=====");		
		Map<String, Object> map = new HashMap<>();

		try{
			String repository = env.getProperty("user.file.upload");
			String fname = "";
			if( file != null && file.getSize() > 0 ) {
				fname = file.getOriginalFilename();
				FileOutputStream fos = new FileOutputStream(new File(repository+File.separator+fname));
				IOUtils.copy(file.getInputStream(), fos);
				fos.close();
			}
			
			Query query = new Query();
			Criteria activityCriteria = Criteria.where("id").is(id);
			query.addCriteria(activityCriteria);
			activityCriteria = Criteria.where("title").is(title);
			query.addCriteria(activityCriteria);
			List<Board> out = mongoTemplate.find(query, Board.class);
			if( out.size() > 0 ){
				Board in = out.get(0);
				in.setTitle(title);
				in.setContents(contents);
				in.setFname(fname);
				boardRepository.save(in);
			}
			
			map.put("returnCode", "success");
			map.put("returnDesc", "");
		}catch(Exception e){
			map.put("returnCode", "failed");
			map.put("returnDesc", "데이터 수정에 실패하였습니다.");
		}
		        
        return map;
    }
	
	@RequestMapping(value="/mod2", method=RequestMethod.POST)
	@ResponseBody
    public Map<String, Object> mod2(@RequestParam(value="id", required=true) String id,
    		@RequestParam(value="title", required=true) String title,
			@RequestParam(value="contents", required=false, defaultValue="") String contents,
			@RequestParam(value="file", required=false) List<MultipartFile> files) throws Exception {
		System.out.println("mod2=====");		
		Map<String, Object> map = new HashMap<>();

		try{
			String repository = env.getProperty("user.file.upload");
			String fnames = "";
			for(MultipartFile file : files){
				if( file != null && file.getSize() > 0 ) {
					String fname = file.getOriginalFilename();
					System.out.println("fname:"+fname);
					FileOutputStream fos = new FileOutputStream(new File(repository+File.separator+fname));
					IOUtils.copy(file.getInputStream(), fos);
					fos.close();
					
					if( "".equals(fnames) ){
						fnames = fname;
					}else{
						fnames += ","+fname;
					}
				}
			}
			
			Query query = new Query();
			Criteria activityCriteria = Criteria.where("id").is(id);
			query.addCriteria(activityCriteria);
			activityCriteria = Criteria.where("title").is(title);
			query.addCriteria(activityCriteria);
			List<Board> out = mongoTemplate.find(query, Board.class);
			if( out.size() > 0 ){
				Board in = out.get(0);
				in.setTitle(title);
				in.setContents(contents);
				in.setFname(fnames);
				boardRepository.save(in);
			}
			
			map.put("returnCode", "success");
			map.put("returnDesc", "");
		}catch(Exception e){
			map.put("returnCode", "failed");
			map.put("returnDesc", "데이터 수정에 실패하였습니다.");
		}
		        
        return map;
    }
	
	@RequestMapping(value="/del", method=RequestMethod.POST)
	@ResponseBody
    public Map<String, Object> del(@RequestParam(value="id", required=true) String id,
    		@RequestParam(value="title", required=true) String title,
			@RequestParam(value="contents", required=false, defaultValue="") String contents) throws Exception {
		System.out.println("del=====");		
		Map<String, Object> map = new HashMap<>();

		try{
			Query query = new Query();
			Criteria activityCriteria = Criteria.where("id").is(id);
			query.addCriteria(activityCriteria);
			activityCriteria = Criteria.where("title").is(title);
			query.addCriteria(activityCriteria);
			List<Board> out = mongoTemplate.find(query, Board.class);
			if( out.size() > 0 ){
				Board in = out.get(0);
				boardRepository.delete(in);
			}
			
			map.put("returnCode", "success");
			map.put("returnDesc", "");
		}catch(Exception e){
			map.put("returnCode", "failed");
			map.put("returnDesc", "데이터 삭제에 실패하였습니다.");
		}
		        
        return map;
    }
	
	@RequestMapping(value="/delimg", method=RequestMethod.POST)
	@ResponseBody
    public Map<String, Object> delimg(@RequestParam(value="id", required=true) String id,
    		@RequestParam(value="title", required=true) String title,
			@RequestParam(value="contents", required=false, defaultValue="") String contents) throws Exception {
		System.out.println("delimg=====");		
		Map<String, Object> map = new HashMap<>();

		try{
			Query query = new Query();
			Criteria activityCriteria = Criteria.where("id").is(id);
			query.addCriteria(activityCriteria);
			activityCriteria = Criteria.where("title").is(title);
			query.addCriteria(activityCriteria);
			List<Board> out = mongoTemplate.find(query, Board.class);
			if( out.size() > 0 ){
				Board in = out.get(0);
				
				String repository = env.getProperty("user.file.upload");
				String fname = repository+File.separator+in.getFname();
				File delFile = new File(fname);
				if( delFile.exists() ) {
					System.out.println("del dir:"+fname);
					delFile.delete();
				}

				in.setFname("");
				boardRepository.save(in);
			}
			
			map.put("returnCode", "success");
			map.put("returnDesc", "");
		}catch(Exception e){
			map.put("returnCode", "failed");
			map.put("returnDesc", "데이터 삭제에 실패하였습니다.");
		}
		        
        return map;
    }
	
	@RequestMapping(value="/img")
	@ResponseBody
	public String getImageWithMediaType(@RequestParam(value="fname", required=false, defaultValue="") String fname,
			HttpServletRequest request, HttpServletResponse response) throws IOException {
		System.out.println("img====="+fname);
		String repository = env.getProperty("user.file.upload");
		
		String base64Encoded = "";
		if(fname.contains(",")) {
			fname = fname.substring(0, fname.indexOf(","));
		}
		fname = repository+File.separator+fname;
		System.out.println("dir:"+fname);
		File file = new File(fname);
		if( file.exists() && file.isFile() ) {
		    InputStream in = new FileInputStream(fname);
		    byte[] bytes = IOUtils.toByteArray(in);
		    byte[] encodeBase64 = Base64.getEncoder().encode(bytes);
		    base64Encoded = new String(encodeBase64, "UTF-8");
		    in.close();
		}
		
	    return base64Encoded;
	}
	
	@RequestMapping(value="/img2")
	@ResponseBody
	public String getImageWithMediaType2(@RequestParam(value="fname", required=false, defaultValue="") String fname,
			HttpServletRequest request, HttpServletResponse response) throws IOException {
		System.out.println("img2====="+fname);
		String repository = env.getProperty("user.file.upload");
		
		String rtnTag = "";
		String base64Encoded = "";
		String[] fnameList = fname.split(",");
		for(int i=0; i<fnameList.length; i++) {
			fname = repository+File.separator+fnameList[i];
			//System.out.println("dir:"+fname);
			File file = new File(fname);
			if( file.exists() && file.isFile() ) {
				InputStream in = new FileInputStream(fname);
			    byte[] bytes = IOUtils.toByteArray(in);
			    byte[] encodeBase64 = Base64.getEncoder().encode(bytes);
			    base64Encoded = new String(encodeBase64, "UTF-8");
			    in.close();
			    
			    rtnTag += "<img src=\"data:image/jpeg;base64,"+base64Encoded+"\" alt=\"image\" style=\"max-width:100%\"/><br/><br/>";
			}
		}
		System.out.println("rtnTag:"+rtnTag);
		
	    return rtnTag;
	}
}
