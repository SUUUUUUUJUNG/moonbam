<%@ page import="java.util.*" %>
<%@ page import="com.moonBam.dto.AdminReportDTO" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
	<style>
		.container {
			padding-top: 20px;
		}
		table {
			width: 100%;
			background-color: #fff;
		}
		th, td {
			text-align: center;
		}
		.btn-pink {
			background-color: #ff416c; /* Bright pink color */
			color: #fff;
			border: none;
		}
		.btn-pink:hover {
			background-color: #ff6392;
		}
		.form-control:focus {
			border-color: #ff416c;
			box-shadow: 0 0 0 0.25rem rgba(255, 65, 108, 0.25);
		}
		.header {
			background-color: #ffccd5; /* Soft pink for header */
			color: #fff;
		}
	</style>
<div class="container">
	<h1 class="text-center">관리자 페이지: 신고된 회원 관리</h1>
	<hr>
	<form action="<%=request.getContextPath()%>/AdminPage/AdminMemberReported" method="post" class="mb-3">
		<div class="input-group mb-3">
			<select name="criteria" class="form-select">
				<option value="userid">회원ID</option>
				<option value="signdate">가입일</option>
				<option value="">처리상태</option>
			</select>
			<input type="text" class="form-control" placeholder="검색조건 입력" name="SearchValue">
			<button type="submit" class="btn btn-pink">검색</button>
		</div>
	</form>
	<table class="table table-bordered">
		<thead class="header">
		<tr>
			<th>&nbsp;</th>
			<th>신고대상</th>
			<th>음란물</th>
			<th>언어</th>
			<th>도배</th>
			<th>규정위반</th>
			<th>기타</th>
			<th>조치</th>
		</tr>
		</thead>
		<tbody>
		<c:forEach var="dto" items="${list}">
			<tr>
				<td><input type="checkbox"></td>
				<td>${dto.userId}</td>
				<td>${dto.sexual}</td>
				<td>${dto.lang}</td>
				<td>${dto.abusing}</td>
				<td>${dto.ruleviolation}</td>
				<td>${dto.etc}</td>
				<td>
					<button class="btn btn-sm btn-pink">정지</button>
					<button class="btn btn-sm btn-pink">강퇴</button>
				</td>
			</tr>
		</c:forEach>
		<c:if test="${list == null}">
			<tr>
				<td colspan="8">검색조건을 입력하십시오.</td>
			</tr>
		</c:if>
		</tbody>
	</table>
	<div>
		<button type="button" class="btn btn-pink">강등</button>
		<button type="button" class="btn btn-pink">정지</button>
		<button type="button" class="btn btn-pink">강퇴</button>
	</div>
</div>
