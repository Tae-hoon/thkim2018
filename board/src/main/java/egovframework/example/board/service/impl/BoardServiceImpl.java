/*
 * Copyright 2008-2009 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package egovframework.example.board.service.impl;

import java.util.List;

import egovframework.example.board.service.BoardService;
import egovframework.example.board.service.BoardVO;

import egovframework.rte.fdl.cmmn.EgovAbstractServiceImpl;
import egovframework.rte.fdl.idgnr.EgovIdGnrService;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * @Class Name : BoardServiceImpl.java
 * @Description : Board Business Implement Class
 * @Modification Information
 * @
 * @  수정일      수정자              수정내용
 * @ ---------   ---------   -------------------------------
 * @ 2009.03.16           최초생성
 *
 * @author 개발프레임웍크 실행환경 개발팀
 * @since 2009. 03.16
 * @version 1.0
 * @see
 *
 *  Copyright (C) by MOPAS All right reserved.
 */

@Service("boardService")
public class BoardServiceImpl extends EgovAbstractServiceImpl implements BoardService {

	private static final Logger LOGGER = LoggerFactory.getLogger(BoardServiceImpl.class);

	/** BoardDAO */
	// TODO ibatis 사용
	//@Resource(name = "boardDAO")
	//private BoardDAO boardDAO;
	// TODO mybatis 사용
	@Resource(name="boardMapper")
	private BoardMapper boardDAO;

	/** ID Generation */
	@Resource(name = "egovIdGnrService")
	private EgovIdGnrService egovIdGnrService;

	/**
	 * 글을 등록한다.
	 * @param vo - 등록할 정보가 담긴 BoardVO
	 * @return 등록 결과
	 * @exception Exception
	 */
	public String insertBoard(BoardVO vo) throws Exception {
		LOGGER.debug(vo.toString());
		
		boardDAO.insertBoard(vo);
		
		if( vo.getFilename() != null && !"".equals(vo.getFilename()) ){
			vo.setIdx(boardDAO.selectBoardIdx(vo));
			boardDAO.insertAttach(vo);
		}
		
		return vo.getIdx();
	}

	/**
	 * 글을 수정한다.
	 * @param vo - 수정할 정보가 담긴 BoardVO
	 * @return void형
	 * @exception Exception
	 */
	public void updateBoard(BoardVO vo) throws Exception {
		
		if( vo.getFilename() != null && !"".equals(vo.getFilename()) ){
			boardDAO.insertAttach(vo);
		}
		
		boardDAO.updateBoard(vo);
	}

	/**
	 * 글을 삭제한다.
	 * @param vo - 삭제할 정보가 담긴 BoardVO
	 * @return void형
	 * @exception Exception
	 */
	public void deleteBoard(BoardVO vo) throws Exception {
		boardDAO.deleteBoard(vo);
	}

	/**
	 * 글을 조회한다.
	 * @param vo - 조회할 정보가 담긴 BoardVO
	 * @return 조회한 글
	 * @exception Exception
	 */
	public BoardVO selectBoard(BoardVO vo) throws Exception {
		BoardVO resultVO = boardDAO.selectBoard(vo);
		if (resultVO == null){
			//throw processException("info.nodata.msg");
			resultVO = new BoardVO();
		}
		return resultVO;
	}

	/**
	 * 글 목록을 조회한다.
	 * @param searchVO - 조회할 정보가 담긴 VO
	 * @return 글 목록
	 * @exception Exception
	 */
	public List<?> selectBoardList(BoardVO searchVO) throws Exception {
		return boardDAO.selectBoardList(searchVO);
	}

	/**
	 * 글 총 갯수를 조회한다.
	 * @param searchVO - 조회할 정보가 담긴 VO
	 * @return 글 총 갯수
	 * @exception
	 */
	public int selectBoardListTotCnt(BoardVO searchVO) {
		return boardDAO.selectBoardListTotCnt(searchVO);
	}

	public String selectLoginCheck(BoardVO searchVO) {
		return boardDAO.selectLoginCheck(searchVO);
	}
	
	public void insertReply(BoardVO vo) throws Exception {
		LOGGER.debug(vo.toString());
		
		boardDAO.insertReply(vo);
	}
	
	public List<?> selectReplyList(BoardVO searchVO) throws Exception {
		return boardDAO.selectReplyList(searchVO);
	}
	
	public void updateBoardCount(BoardVO vo) throws Exception {
		boardDAO.updateBoardCount(vo);
	}
	
	public void insertAttach(BoardVO vo) throws Exception {
		LOGGER.debug(vo.toString());
		
		boardDAO.insertAttach(vo);
	}
	
	public void deleteAttach(BoardVO vo) throws Exception {
		boardDAO.deleteAttach(vo);
	}
	
	public BoardVO selectAttach(BoardVO vo) throws Exception {
		BoardVO resultVO = boardDAO.selectAttach(vo);
		if (resultVO == null){
			//throw processException("info.nodata.msg");
			resultVO = new BoardVO();
		}
		return resultVO;
	}
}
