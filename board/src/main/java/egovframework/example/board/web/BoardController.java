package egovframework.example.board.web;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.URLDecoder;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.oreilly.servlet.MultipartRequest;

import egovframework.com.cmm.EgovWebUtil;
import egovframework.com.cmm.service.EgovFileMngUtil;
import egovframework.com.cmm.service.EgovProperties;
import egovframework.com.cmm.service.Globals;
import egovframework.example.board.service.BoardService;
import egovframework.example.board.service.BoardVO;
import egovframework.example.sample.service.SampleDefaultVO;
import egovframework.rte.fdl.property.EgovPropertyService;
import egovframework.rte.ptl.mvc.tags.ui.pagination.PaginationInfo;

@Controller
public class BoardController {
	
	@Resource(name = "boardService")
	private BoardService boardService;

	/** EgovPropertyService */
	@Resource(name = "propertiesService")
	protected EgovPropertyService propertiesService;

	@RequestMapping(value = "/list.do")
	public String list(@ModelAttribute("boardVO") BoardVO boardVO, ModelMap model) throws Exception {
		
		/** EgovPropertyService.sample */
		boardVO.setPageUnit(propertiesService.getInt("pageUnit"));
		boardVO.setPageSize(propertiesService.getInt("pageSize"));

		/** pageing setting */
		PaginationInfo paginationInfo = new PaginationInfo();
		paginationInfo.setCurrentPageNo(boardVO.getPageIndex());
		paginationInfo.setRecordCountPerPage(boardVO.getPageUnit());
		paginationInfo.setPageSize(boardVO.getPageSize());

		boardVO.setFirstIndex(paginationInfo.getFirstRecordIndex());
		boardVO.setLastIndex(paginationInfo.getLastRecordIndex());
		boardVO.setRecordCountPerPage(paginationInfo.getRecordCountPerPage());

		List<?> list = boardService.selectBoardList(boardVO);
		model.addAttribute("resultList", list);

		int totCnt = boardService.selectBoardListTotCnt(boardVO);
		paginationInfo.setTotalRecordCount(totCnt);
		model.addAttribute("paginationInfo", paginationInfo);
		
		model.addAttribute("searchKeyword", boardVO.getSearchKeyword());
		
		return "board/list";
	}
	
	@RequestMapping(value = "/mgmt.do", method = RequestMethod.GET)
	public String mgmt(@ModelAttribute("boardVO") BoardVO boardVO, ModelMap model, HttpServletRequest request) throws Exception {
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		Calendar c1 = Calendar.getInstance();
		String strToday = sdf.format(c1.getTime());
		System.out.println("Today=" + strToday);
		
		//BoardVO boardVO = new BoardVO();
		boardVO = boardService.selectBoard(boardVO);
		
		BoardVO vo = boardService.selectAttach(boardVO);
		boardVO.setFilename(vo.getFilename());
		boardVO.setSeq(vo.getSeq());
		
		boardVO.setIndate(strToday);
		if( request.getSession().getAttribute("userId") != null ){
			boardVO.setWriter(request.getSession().getAttribute("userId").toString());
		}
		if( request.getSession().getAttribute("userName") != null ){
			boardVO.setWriterNm(request.getSession().getAttribute("userName").toString());
		}
		
		model.addAttribute("boardVO", boardVO);
		
		return "board/mgmt";
	}
	
	@RequestMapping(value = "/mgmtAdd.do", method = RequestMethod.POST)
	public String mgmtAdd(@ModelAttribute("boardVO") BoardVO boardVO, 
			@RequestParam("mode") String mode, ModelMap model) throws Exception {
		
		if( "add".equals(mode) ){
			boardService.insertBoard(boardVO);
		}else if( "mod".equals(mode) ){
			boardService.updateBoard(boardVO);
		}else if( "del".equals(mode) ){
			boardService.deleteBoard(boardVO);
		}
		
		return "redirect:/list.do";
	}
	
	@RequestMapping(value = "/mgmt.do", method = RequestMethod.POST)
	public String mgmt2(@ModelAttribute("boardVO") BoardVO boardVO, 
			@RequestParam("mode") String mode, ModelMap model,
			HttpServletRequest request) throws Exception {
		
		MultipartHttpServletRequest mptRequest = null;
		try {
			mptRequest = (MultipartHttpServletRequest) request;
			//MultipartRequest mptRequest = (MultipartRequest) request;
		}catch(Exception e){
			//e.printStackTrace();
		}
		
		if( mptRequest != null ){
			Iterator fileIter = mptRequest.getFileNames();
			 
			while (fileIter.hasNext()) {
				MultipartFile mFile = mptRequest.getFile((String)fileIter.next());
				System.out.println("filename:"+mFile.getOriginalFilename());
			 
				if (mFile.getSize() > 0) {
					
					if( "mod".equals(mode) ){
						fileDeleteSub(request);
					}
					
					HashMap map = EgovFileMngUtil.uploadFile(mFile);
			 
					System.out.println("[ "+Globals.FILE_PATH+" : "+map.get(Globals.FILE_PATH)+" ]");
					System.out.println("[ "+Globals.FILE_SIZE+" : "+map.get(Globals.FILE_SIZE)+" ]");
					System.out.println("[ "+Globals.ORIGIN_FILE_NM+" : "+map.get(Globals.ORIGIN_FILE_NM)+" ]");
					System.out.println("[ "+Globals.UPLOAD_FILE_NM+" : "+map.get(Globals.UPLOAD_FILE_NM)+" ]");
					System.out.println("[ "+Globals.FILE_EXT+" : "+map.get(Globals.FILE_EXT)+" ]");
					
					boardVO.setFilename(map.get(Globals.UPLOAD_FILE_NM).toString());
				}
			}
		}
		
		if( "add".equals(mode) ){
			boardService.insertBoard(boardVO);
		}else if( "mod".equals(mode) ){
			boardService.updateBoard(boardVO);
		}else if( "del".equals(mode) ){
			boardService.deleteBoard(boardVO);
		}
		
		return "redirect:/list.do";
	}
	
	@RequestMapping(value = "/fileGet.do", method = RequestMethod.GET)
	public void fileDownload(HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		String stordFilePath = EgovProperties.getProperty("Globals.fileStorePath");
		 
		// 저장된 파일명
		String filename = request.getParameter("filename");
		//System.out.println("filename:"+filename);
		//filename = URLDecoder.decode(filename, "UTF-8");
		//System.out.println("filename2:"+filename);
		// 첨부된 원 파일명
		String original = request.getParameter("original");
		 
		//if ("".equals(original)) {
			original = filename;
		//}
		 
		request.setAttribute("downFile", stordFilePath + filename);
		request.setAttribute("orginFile", original);
		
		//System.out.println("download:"+(stordFilePath + filename));
		 
		EgovFileMngUtil.downFile(request, response);
	}
	
	@RequestMapping(value = "/fileDel.do", method = RequestMethod.GET)
	public String fileDelete(HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		String idx = fileDeleteSub(request);
		
		return "redirect:/view.do?idx="+idx;
	}
	
	private String fileDeleteSub(HttpServletRequest request) throws Exception {
		String stordFilePath = EgovProperties.getProperty("Globals.fileStorePath");
		 
		// 저장된 파일명
		String filename = request.getParameter("filename");
		 
		File file = new File(stordFilePath + filename);
		if (file.exists()) {
			file.delete();
		}
		
		//첨부파일 데이터 삭제
		String idx = request.getParameter("idx");
		String seq = request.getParameter("seq");
		
		BoardVO vo = new BoardVO();
		vo.setIdx(idx);
		vo.setSeq(seq);
		boardService.deleteAttach(vo);
		
		return idx;
	}

	
	@RequestMapping(value = "/fileAdd.do", method = RequestMethod.POST)
	@ResponseBody
	public String fileUpload(MultipartHttpServletRequest multipartRequest, HttpServletRequest request) throws Exception {
		String path = "";
		String fileName = "";
		
		//요즘방식		
		OutputStream out = null;
		PrintWriter printWriter = null;
		
		try {
			Iterator<String> itr =  multipartRequest.getFileNames();
	        while (itr.hasNext()) { //받은 파일들을 모두 돌린다.
	        	
	        	String formName = itr.next();
	        	//System.out.println("formName: "+formName);
	        	MultipartFile uploadFile = multipartRequest.getFile(formName);
			
				try {
					fileName = uploadFile.getOriginalFilename();
					byte[] bytes = uploadFile.getBytes();
					path = getSaveLocation(multipartRequest);
					
					//System.out.println("UtilFile fileUpload fileName : " + fileName);
					//System.out.println("UtilFile fileUpload uploadPath : " + path);
					
					File file = new File(path);
					
					//파일명이 중복으로 존재할 경우
					if (fileName != null && !fileName.equals("")) {
						if (file.exists()) {
							//파일명 앞에 업로드 시간 초단위로 붙여 파일명 중복을 방지
							fileName = System.currentTimeMillis() + "_" + fileName;
							
							file = new File(path + fileName);
						}
					}
					
					//System.out.println("UtilFile fileUpload final fileName : " + fileName);
					//System.out.println("UtilFile fileUpload file : " + file);
					
					out = new FileOutputStream(file);
					
					//System.out.println("UtilFile fileUpload out : " + out);
					
					out.write(bytes);
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					try {
						if (out != null) {
							out.close();
						}
						if (printWriter != null) {
							printWriter.close();
						}
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
	        }
		}catch(Exception e){
			e.printStackTrace();
			throw new RuntimeException(e);
		}
		
		
		/*// 멀티파트 셋팅(옛날방식)
		MultipartRequest parser=null;
		int maxPostSize = 10 * 1024 * 1024; //10메가
		String rootPath = getSaveLocation(request); //업로드 폴더 반환
		
		try {
			
			File dir = new File(rootPath);
	        dir.mkdirs();
	        parser = new MultipartRequest(request, rootPath, maxPostSize, "utf-8", new com.oreilly.servlet.multipart.DefaultFileRenamePolicy());
	        
	        Enumeration formNames=null;
			String formName="";
			formNames=parser.getFileNames();
			
			while(formNames.hasMoreElements())
			{
				formName=(String)formNames.nextElement(); 
	
				String filename = (parser.getFilesystemName(formName) == null)?"":parser.getFilesystemName(formName);
				
				fileName = filename;
			}
			if( !"".equals(fileName) ){
				path = rootPath;
			}
		}catch(Exception e){
			e.printStackTrace();
			throw new RuntimeException(e);
		}*/
		
		

		System.out.println("업로드경로:"+(path+fileName));
		return path + fileName;
	}
	
	private String getSaveLocation(HttpServletRequest request) {
        
        //String uploadPath = request.getSession().getServletContext().getRealPath("/");
        String uploadPath = EgovProperties.getProperty("Globals.fileStorePath");
        String attachPath = "";
        
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM");
		Calendar c1 = Calendar.getInstance();
		String folder = sdf.format(c1.getTime());
		//System.out.println("folder=" + folder);
		attachPath = folder+"/";
		
		File cFile = new File(EgovWebUtil.filePathBlackList(uploadPath+attachPath));
		cFile.mkdir();
        
        System.out.println("UtilFile getSaveLocation path : " + uploadPath + attachPath);
        
        return attachPath;
    }
	
	@RequestMapping(value = "/view.do")
	public String view(@ModelAttribute("boardVO") BoardVO boardVO, ModelMap model) throws Exception {
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		Calendar c1 = Calendar.getInstance();
		String strToday = sdf.format(c1.getTime());
		
		boardService.updateBoardCount(boardVO);
		
		boardVO = boardService.selectBoard(boardVO);
		
		BoardVO vo = boardService.selectAttach(boardVO);
		boardVO.setFilename(vo.getFilename());
		boardVO.setSeq(vo.getSeq());
		
		model.addAttribute("boardVO", boardVO);
		model.addAttribute("strToday",strToday);
		
		List<?> list = boardService.selectReplyList(boardVO);
		model.addAttribute("resultList", list);
		
		return "board/view";
	}
	
	@RequestMapping(value = "/reply.do", method = RequestMethod.POST)
	public String reply(@ModelAttribute("boardVO") BoardVO boardVO, ModelMap model) throws Exception {
		
		boardService.insertReply(boardVO);
		
		return "redirect:/view.do?idx="+boardVO.getIdx();
	}
	
	@RequestMapping(value = "/login.do")
	public String login(@RequestParam("user_id") String user_id, @RequestParam("password") String password, 
			ModelMap model, HttpServletRequest request) throws Exception {
		
		//String aa = request.getParameter("user_id");
		//String bb = request.getParameter("password");
		
		System.out.println("user_id:"+user_id);
		System.out.println("password:"+password);
		
		BoardVO boardVO = new BoardVO();
		boardVO.setUserId(user_id);
		boardVO.setPassword(password);
		String user_name = boardService.selectLoginCheck(boardVO);
		
		if( user_name != null && !"".equals(user_name) ){
			request.getSession().setAttribute("userId", user_id);
			request.getSession().setAttribute("userName", user_name);
		}else{
			request.getSession().setAttribute("userId", "");
			request.getSession().setAttribute("userName", "");
			model.addAttribute("msg", "사용자 정보가 올바르지 않습니다.");
		}
		
		return "redirect:/list.do";
	}
	
	@RequestMapping(value = "/logout.do")
	public String logout(ModelMap model, HttpServletRequest request) throws Exception {
		
		request.getSession().invalidate();
		
		return "redirect:/list.do";
	}
}
