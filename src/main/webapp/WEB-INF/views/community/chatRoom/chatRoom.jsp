<%@ page language="java" contentType="text/html; charset=EUC-KR"
	pageEncoding="EUC-KR"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<head>
<meta charset="EUC-KR">
<title>Chat Room</title>
<script
	src="https://ajax.googleapis.com/ajax/libs/jquery/3.7.1/jquery.min.js"></script>
<script
	src="https://cdnjs.cloudflare.com/ajax/libs/sockjs-client/1.5.0/sockjs.min.js"></script>
<script
	src="https://cdnjs.cloudflare.com/ajax/libs/stomp.js/2.3.3/stomp.min.js"></script>
</head>
<body>
	<h1>chatRoom - ${text}</h1>
	<form id="chatForm">
		<table border="1" style="background: white; width: 500px">
			<thead>
				<tr>
					<td>
						<!-- ����, ��� ��ư --> <input type="checkbox" id="toggle" hidden>
						<label for="toggle" class="toggleSwitch"> <span
							id="toggleIcon" class="toggleButton">�� ${title}</span>
					</label>
					</td>
					<td style="width: 20px;">����</td>
				</tr>
				<tr>
					<!-- ù ȭ����� ���� ������ -->
					<td class="text_align_c" id='toggle_state'><br> <c:if
							test="${sen.lastCv==1}">
						</c:if> <c:if test="${sen.lastCv==0}">
						</c:if></td>
				</tr>
			</thead>
			<tbody>
				<tr>
					<td colspan="3" style="float: left; width: 50%;">
						<table>
							<tr>
								<td><span id="user" style="cursor: pointer;"
									onclick="openMemberWindow()">user</span></td>
								<td>yy/mm/dd/hh:mm</td>
							</tr>
							<tr>
								<td><span id="msg" style="cursor: pointer;"
									onclick="openReportWindow()">your msg</span></td>
							</tr>
						</table>
					</td>
				</tr>
				<tr>
					<td colspan="3" style="float: right; width: 50%;">
						<table>
							<tr>
								<td>yy/mm/dd/hh:mm</td>
							</tr>
							<tr>
								<td style="word-wrap: break-word">my msg</td>
							</tr>
						</table>
					</td>
				</tr>
			</tbody>
			<tfoot>
				<tr>
					<td colspan="2"><input type="text" name="text"
						style="width: 85%"> <input type="button" id="btnSubmit"
						value="����"></td>
				</tr>
			</tfoot>
		</table>
	</form>



	<script>
		/* ��� ó�� */
		$('input[id="toggle"]').change(function() {
			var value = $(this).val();
			var checked = $(this).prop('checked');
			var toggle_state;
			if (checked) {
				document.getElementById('toggleIcon').innerHTML = "�� ${title}";
				document.getElementById('toggle_state').innerHTML = "${text}";
				toggle_state = "on";
			} else {
				document.getElementById('toggleIcon').innerHTML = "�� ${title}";
				document.getElementById('toggle_state').innerHTML = "&nbsp;";
				toggle_state = "off";
			}
		});

		/* �Ű��ϱ� */
		function openReportWindow() {
			var url = "reportWindow";
			window.open(url, "_blank", "width=600,height=400");
		}

		/* ��� */
		function openMemberWindow() {
			var url = "memberWindow";
			window.open(url, "_blank", "width=600,height=400");
		}

		$(document).ready(function() {
			$("#btnSubmit").click(function() {
				submitMessage();
			});
		});

		

		var stompClient = null;

		// ���� ����
		function connect() {
			var socket = new SockJS('/chat-socket');
			stompClient = Stomp.over(socket);
			stompClient.connect({}, function(frame) {
				console.log('Connected: ' + frame);
				stompClient.subscribe('/topic/messages',
						function(messageOutput) {
							showMessageOutput(JSON.parse(messageOutput.body));
						});
			});
		}
		
		// ���� ���� ����
		function disconnect() {
            if (stompClient !== null) {
                stompClient.disconnect();
            }
            console.log("Disconnected");
        }
		
		
		/* �޽��� ���� */
		function sendMessage() {
            var chatRoomId = $('#chatRoomId').val();
            var messageContent = $('#messageContent').val();
            var senderId = $('#senderId').val();
            var messageType = $('#messageType').val();
            stompClient.send("/app/chat/send", {}, JSON.stringify({
                'chatRoomId': chatRoomId,
                'senderId': senderId,
                'message': messageContent,
                'messageType': messageType
            }));
        }
		
		// �޼��� ���
		 function showMessageOutput(messageOutput) {
            $("#messages").append("<tr><td>" + messageOutput.chatRoomId + "</td><td>" + messageOutput.message + "</td></tr>");
        }
		
		 $(function () {
	            $("form").on('submit', function (e) {
	                e.preventDefault();
	            });
	            $( "#connect" ).click(function() { connect(); });
	            $( "#disconnect" ).click(function() { disconnect(); });
	            $( "#send" ).click(function() { sendMessage(); });
	        });
	</script>
</body>
</html>