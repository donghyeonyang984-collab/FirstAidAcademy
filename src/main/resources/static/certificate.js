async function openCertificateWindow(enrollmentId) {
    try {
        // 🔵 1) API 호출
        const response = await fetch(`/api/certificate/${enrollmentId}`);
        const data = await response.json();

        // 🔵 2) 새 창 생성
        const printWin = window.open("", "_blank", "width=900,height=1200");

        // 🔵 3) HTML 텍스트 생성
        const htmlContent = `
<!DOCTYPE html>
<html lang="ko">
<head>
<meta charset="UTF-8">
<title>수료증</title>
<link rel="stylesheet" href="/certificate.css">
</head>
<body>
    <div class="certificate_wrap">
        <div class="certificate_frame">
            <div class="certificate_content">

                <div class="cert_number">제 ${data.certificateNumber} 호</div>

                <h1 class="cert_title">교육이수증명서</h1>

                <div class="cert_info">
                    <p><strong>소속:</strong> First Aid 아카데미</p>
                    <p><strong>성명:</strong> ${data.userName}</p>
                </div>

                <div class="cert_course">
                    <p><strong>과정명:</strong> ${data.courseTitle}</p>
                    <p><strong>기간:</strong> ${data.startDate} ~ ${data.endDate}</p>
                    <p><strong>이수과목:</strong></p>
                    <ul>
                        <li>${data.courseTitle}</li>
                    </ul>
                </div>

                <p class="cert_statement">
                    위 사람은 본 기관이 실시한 상기 교육 과정을 성실히 이수하였으므로<br>
                    이 증서를 수여함.
                </p>

                <p class="cert_date">${data.issuedDate}</p>

                <div class="cert_footer">
                    <p class="cert_org">First Aid 아카데미</p>
                    <div class="cert_stamp"></div>
                </div>

            </div>
        </div>
    </div>

<script>
    window.onload = function() {
        window.print();
    };
</script>

</body>
</html>
        `;

        // 🔵 4) 새 창에 HTML 삽입
        printWin.document.open();
        printWin.document.write(htmlContent);
        printWin.document.close();

    } catch (e) {
        console.error("수료증 생성 실패:", e);
        alert("수료증을 불러오지 못했습니다.");
    }
}
