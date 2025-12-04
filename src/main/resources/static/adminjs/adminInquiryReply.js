// /adminjs/adminInquiryReply.js
document.addEventListener("DOMContentLoaded", () => {
    const replyForm = document.getElementById("admin_inq_reply_form");
    const replyContent = document.getElementById("replyContent");

    if (!replyForm || !replyContent) {
        return;
    }

    // 답변 등록 시 기본 검증만 수행
    replyForm.addEventListener("submit", (e) => {
        const content = replyContent.value.trim();

        if (!content) {
            e.preventDefault();
            alert("답변 내용을 입력하세요.");
            replyContent.focus();
        }
        // 내용이 있으면 그대로 서버로 POST → 컨트롤러에서 저장
    });
});
