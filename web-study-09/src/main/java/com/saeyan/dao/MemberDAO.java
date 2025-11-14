package com.saeyan.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import com.saeyan.dto.MemberVO;

/*
	DB link class
	싱글톤으로 객체 하나만 생성해서 공유
*/

public class MemberDAO {
	
	// 1. 싱글톤 패턴 : 객체를 하나만 생성
	private static MemberDAO instance = new MemberDAO();
	
	// 2. 외부에서 생성자 호출 못하도록 private
	private MemberDAO() {}
	
	// 3. 외부에서 사용할 수 있도록 단일 인스턴스 제공
	public static MemberDAO getInstance() {
		return instance;
	}
	
	// 4. DB 연결 메소드(JDBC)
	public Connection getConnection() {
		
		Connection con = null;
		
		try {
			// 드라이브 로드
			Class.forName("oracle.jdbc.driver.OracleDriver"); 
			
			// DB 연결 
			con = DriverManager.getConnection(
					"jdbc:oracle:thin:@localhost:1521:xe",
					"test",
					"1234"
					);
			
		}catch(Exception e) {
			e.printStackTrace();
			
		}// try
		
		return con;
		
	}// getConnection
	
	// 사용자 인증시 사용 메소드
	public boolean userCheck(String userid, String pwd) {
		
		boolean result = false;
		
		String sql = "select pwd from member where userid = ?";
		Connection con = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		try {
			
			con = getConnection(); // DB연결
			pstmt = con.prepareStatement(sql);// sql 구문 전송, sql 에러있는지 체크
			
			pstmt.setString(1, userid); //매핑
			
			rs = pstmt.executeQuery(); // 실행 및 결과 반환
			
			if(rs.next()) { // 가져올 데이터 확인
				
					// null 값인지 확인					비교해서 맞는지 확인
				if(rs.getString("pwd") != null && rs.getString("pwd").equals(pwd)) {
					result = true;	// 맞으면 true
				}
			}// if
			
		}catch(Exception e){
			e.printStackTrace();
		}finally{
				try {
					rs.close();
					pstmt.close();
					con.close();
				} catch (Exception e) {
					e.printStackTrace();
					
				}// try2
		}// try1
		
		return result;
		
	}// end userCheck
	
	// 아이디로 회원 정보 가져오는 메소드
	public MemberVO getMember(String userid) {
		
		MemberVO mvo = new MemberVO();
		String sql = "select * from member where userid = ?";
		
		Connection con = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		try {
			con = getConnection();
			pstmt = con.prepareStatement(sql);
			
			//매핑
			pstmt.setString(1, userid);
			
			rs = pstmt.executeQuery();
			
			if(rs.next()) {
				
				// 값 꺼내오기
				mvo.setName(rs.getString("name"));
				mvo.setUserid(rs.getString("userid"));
				mvo.setPwd(rs.getString("pwd"));
				mvo.setEmail(rs.getString("email"));
				mvo.setPhone(rs.getString("phone"));
				mvo.setAdmin(rs.getInt("admin"));
				
			}
			
		}catch(Exception e){
			e.printStackTrace();
			
		}finally {
			
			try {
				rs.close();
				pstmt.close();
				con.close();
				
			}catch(Exception e) {
				e.printStackTrace();
			}
		} //finally
		
		return mvo;
		
	}// end getMember

	
	// 아이디 중복체크
	// result = -1 아이디 사용 가능
	// result = 1 사용 불가능
	public int confirmID(String userid) {
		
		int result = -1;
		
		String sql = "select userid from member where userid = ?";
		Connection con = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		try {
			
			con = getConnection();
			pstmt = con.prepareStatement(sql);
			
			pstmt.setString(1, userid); 
			
			rs = pstmt.executeQuery(); 
			
			if(rs.next()) { 
				result = 1;
				
			}// end if
			
		}catch(Exception e){
			e.printStackTrace();
		}finally{
				try {
					rs.close();
					pstmt.close();
					con.close();
				} catch (Exception e) {
					e.printStackTrace();
					
				}
				
		}// end finally
		
		return result;		
		
	}// end confirmID
}
