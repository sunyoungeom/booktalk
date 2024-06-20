 // 비밀번호 일치 여부 확인
 function checkPasswordMatch() {
    var password = document.getElementById('password').value;
    var passwordConfirm = document.getElementById('passwordConfirm').value;
    var passwordError = document.getElementById('passwordError');

    if (password === passwordConfirm) {
      passwordError.style.display = 'none';
      return true;
    } else {
      passwordError.style.display = 'block';
      return false;
    }
  }

  // 비밀번호 유효성 검사
function validatePassword(password) {
  return /^[a-zA-Z0-9]+$/.test(password);
}

  // 회원 가입
  function submitForm(event) {
    event.preventDefault();

    var nickname = document.getElementById('nickname').value;
    var email = document.getElementById('email').value;
    var password = document.getElementById('password').value;
    var checkBox = document.getElementById("agreeCheckbox");

    if (!nickname) {
      alert("닉네임을 입력하세요.");
      return;
    }
    if (!email) {
      alert("이메일을 입력하세요.");
      return;
    }
    if (!password) {
      alert("비밀번호를 입력하세요.");
      return;
    }
    if (!checkPasswordMatch()) {
      alert("비밀번호와 비밀번호 확인이 일치하지 않습니다.");
      return;
    }
    if (!checkBox.checked) {
      alert("가입에 동의해주세요.");
      return;
    }
    if (!validatePassword(password)) {
      alert("비밀번호는 영어와 숫자로만 구성되어야 합니다.");
      return;
    }

    var data = {
      "nickname": nickname,
      "email": email,
      "password": password,
    };

    fetch('/api/user', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify(data)
    })
      .then(response => {
        if (!response.ok) {
          if (response.status === 409) {
            return response.json().then(json => {
              const errorMessage = json.message;
              alert(errorMessage)

              throw new Error(errorMessage);
            });
          }
          throw new Error('서버에서 오류 응답을 받았습니다.');
        }
        alert("가입이 완료되었습니다.");
        window.location.href = '/user/login';
      })
      .catch(error => {
        console.error(error);
      });
  }

  document.addEventListener("DOMContentLoaded", () => {
    const password = document.getElementById('password');
    const passwordConfirm = document.getElementById('passwordConfirm');
    const passwordError = document.getElementById('passwordError');

    password.addEventListener('input', checkPasswordMatch);
    passwordConfirm.addEventListener('input', checkPasswordMatch);
  });
