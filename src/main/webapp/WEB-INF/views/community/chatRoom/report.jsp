<%@ page language="java" contentType="text/html; charset=EUC-KR"
	pageEncoding="EUC-KR"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="EUC-KR">
<title>��ȭ�ε��� ��</title>
</head>
<body>
	�� ��ȭ�� �Ű��մϴ�.
	<br>
	<form id="reportForm" action="chatReport" method="post">
		<input type="submit" value="�Ű�"> 
		 <input type="button" onclick="cancelReport()" value="���">
	</form>

	<script
		src="https://ajax.googleapis.com/ajax/libs/jquery/3.7.1/jquery.min.js"></script>
	<script>
	// ����ϱ� - â �ݱ�   
	function cancelReport() {
           window.close();
       }
	// �Ű��ϱ� - â �ݱ�
	// �� ���� �̺�Ʈ ó��
        $("#reportForm").submit(function(event) {
            event.preventDefault(); // �⺻ ���� ���� ����
            var formData = $(this).serialize(); // �� ������ ����ȭ

            // Ajax�� ����Ͽ� �����͸� chatReport �������� ����
            $.ajax({
                type: "POST",
                url: "chatReport",
// ���� ������ ���� �ʿ�
                data: formData,
                success: function(response) {

                    // ������ �����ϸ� ���� â�� ����
                    window.close();
                },
                error: function(xhr, status, error) {
                    // ���� ���� ó��
                    console.error(xhr.responseText);
                }
            });
        });
	   
</script>
</body>
</html>