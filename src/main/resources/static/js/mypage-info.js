const id = document.getElementById('userId').value;

// 유저 정보 로드
function loadUserInfo() {

  fetch('/api/user/' + id, {
  })
    .then(response => {
      if (!response.ok) {
        throw new Error('조회시 오류 응답을 받았습니다.');
      }
      return response.json();
    })
    .then(data => {
      // TODO: 프로필 사진
      const profileImgPath = document.getElementById('profileImgPath');
      const nickname = document.getElementById('nickname');
      const email = document.getElementById('email');
      const signUpType = document.getElementById('signUpType');
      const signUpDate = document.getElementById('signUpDate');

      nickname.textContent = data.nickname;
      email.textContent = data.email;
      signUpType.textContent = data.signUpType;
      signUpDate.textContent = data.signUpDate;
    })
    .catch(error => {
      console.error(error);
    });
}

// 회원 정보 수정
function modifyUserInfo(field) {
  openModal(field);
}

function cancelModal() {
  closeModal();

  window.postMessage({ type: 'closeModal', payload: '취소되었습니다.' }, '*');
}

// 비밀번호 유효성 검사
function validatePassword(password) {
  return /^[a-zA-Z0-9]+$/.test(password);
}

function confirmEdit() {
  const fieldValue = document.getElementById('fieldValue').value;
  const currentValue = document.getElementById('currentValue').value;
  const newValue = document.getElementById('newValue').value;

  let data = {};

  if (fieldValue === 'password') {
    if (!validatePassword(newValue)) {
      alert("비밀번호는 영어와 숫자로만 구성되어야 합니다.");
      return;
    }
    data = {
      currentPassword: currentValue,
      newPassword: newValue
    };
    console.log(data)
  }

  if (fieldValue === 'nickname') {
    data = {
      nickname: newValue
    };
  }

  fetch(`/api/user/` + id, {
    method: 'PATCH',
    headers: {
      'Content-Type': 'application/json'
    },
    body: JSON.stringify(data)
  })

    .then(response => {
      if (!response.ok) {
        if (response.status === 400) {
          return response.json().then(json => {
            const errorMessage = json.message;
            alert(errorMessage)

            throw new Error(errorMessage);
          });
        }
        throw new Error('서버에서 오류 응답을 받았습니다.');
      }
      return response.json();
    })
    .then(data => {
      // 닉네임 수정 성공
      if (data.nickname) {
        const nickname = document.getElementById('nickname');
        nickname.textContent = data.nickname;
      }
      // 비밀번호 수정 성공
      if (data.message) {
        alert(data.message);
      }
      closeModal();
    })
    .catch(error => {
      console.error(error);
    });
}

function openModal(field) {
  const modal = document.getElementById('editModal');
  fetch('/edit/' + field)
    .then(response => response.text())
    .then(html => {
      modal.style.display = 'block';
      modal.innerHTML = html;
    });
}

function closeModal() {
  const modal = document.getElementById('editModal');
  modal.innerHTML = "";
  modal.style.display = 'none';
}

// 회원 탈퇴
function withdrawal() {
  if (confirm("정말로 탈퇴하시겠습니까?")) {
    fetch('/api/user/' + id, {
      method: 'DELETE'
    })
      .then(response => {
        if (!response.ok) {
          throw new Error('탈퇴시 오류 응답을 받았습니다.');
        }
        return response.json();
      })
      .then(data => {
        alert(data.message)
      })
      .catch(error => {
        console.error(error);
      });
  } else {
    console.log("탈퇴가 취소되었습니다.");
  }
}

document.addEventListener("DOMContentLoaded", () => {
  loadUserInfo();
});