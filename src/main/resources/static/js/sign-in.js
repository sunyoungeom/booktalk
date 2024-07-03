  // 로그인
  function submitForm(event) {
    event.preventDefault();

    var email = document.getElementById('email').value;
    var passwordField = document.getElementById('password');
    var password = passwordField.value;

    if (!email) {
      alert("이메일을 입력하세요.");
      return;
    }
    if (!password) {
      alert("비밀번호를 입력하세요.");
      return;
    }

    var data = {
      "email": email,
      "password": password
    };

    fetch('/user/login', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify(data)
    })
      .then(response => {
        if (!response.ok) {
          // 존재하지 않는 사용자
          if (response.status === 404) {
            return response.json().then(json => {
              const errorMessage = json.message;
              alert(errorMessage)

              throw new Error(errorMessage);
            });
          }
          // 비밀번호 오류
          if (response.status === 400) {
            return response.json().then(json => {
              const errorMessage = json.message;
              alert(errorMessage)
              // 비밀번호 필드 값 비우기
              passwordField.value = '';

              throw new Error(errorMessage);
            });
          }
          throw new Error('서버에서 오류 응답을 받았습니다.');
        }
        return response.json();

      })
      .then(data => {
        alert("로그인에 성공했습니다.");

        sessionStorage.setItem('profileImgPath', data.profileImgPath);
        sessionStorage.setItem('username', data.nickname);

        window.location.href = '/';
      })
      .catch(error => {
        console.error(error);
      });
  }
