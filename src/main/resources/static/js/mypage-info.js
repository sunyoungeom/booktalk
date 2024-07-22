const id = document.getElementById('session-userId').value;

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
      const profileImg = document.getElementById('profileImgPath');
      const nickname = document.getElementById('nickname');
      const email = document.getElementById('email');
      const signUpType = document.getElementById('signUpType');
      const signUpDate = document.getElementById('signUpDate');

      profileImg.src = data.data.profileImgPath;
      nickname.textContent = data.data.nickname;
      email.textContent = data.data.email;
      signUpType.textContent = data.data.signUpType;
      signUpDate.textContent = data.data.signUpDate;
    })
    .catch(error => {
      console.error(error);
    });
}

// 회원 정보 수정
function modifyUserInfo(field) {
  openModal(field);
}

function confirmEdit() {
  const fieldValue = document.getElementById('fieldValue').value;
  const currentValue = document.getElementById('currentValue').value;
  const newValue = document.getElementById('newValue').value;

  let data = {};

  if (fieldValue === 'profileImgPath') {
    uploadImg();
    return;
  }

  if (fieldValue === 'nickname') {
    if (!noSpace(newValue)) {
      alert("닉네임은 공백을 포함할 수 없습니다.");
      return;
    }
    if (!validateNickname(newValue)) {
      alert("닉네임은 특수문자를 포함하지 않으며 6자 이내여야 합니다.");
      return;
    }
    data = {
      newNickname: newValue
    };
  }

  if (fieldValue === 'password') {
    if (!noSpace(newValue)) {
      alert("비밀번호는 공백을 포함할 수 없습니다.");
      return;
    }
    if (!validatePassword(newValue)) {
      alert("비밀번호는 10자 이내의 숫자와 영소문자로 이루어져야 합니다.");
      return;
    }
    data = {
      currentPassword: currentValue,
      newPassword: newValue
    };
  }

  fetch(`/api/user/${id}/${fieldValue}`, {
    method: 'PATCH',
    headers: {
      'Content-Type': 'application/json'
    },
    body: JSON.stringify(data)
  })
    .then(response => {
      if (!response.ok) {
        if (response.status === 400 || response.status === 409) {
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
    console.log(data)
      // 닉네임 수정 성공
      if (fieldValue === 'nickname') {
        const nickname = document.getElementById('nickname');
        nickname.textContent = data.data;
        alert(data.message);
      }
      // 비밀번호 수정 성공
      if (fieldValue === 'password') {
        alert(data.message);
      }
      closeModal();
    })
    .catch(error => {
      console.error(error);
    });
}

function uploadImg() {
  const profileImg = document.getElementById('newValue');
  const file = profileImg.files[0];
  if (!file) {
    alert('파일을 선택해주세요.');
    return;
  }
  const formData = new FormData();
  formData.append('file', file);
  fetch(`/api/user/${id}/profileImg`, {
    method: 'PATCH',
    body: formData
  })
    .then(response => {
      if (!response.ok) {
        throw new Error('파일 업로드에 실패하였습니다.');
      }
      return response.json();
    })
    .then(data => {
      // 프로필 사진 수정 성공
      const profileImg = document.getElementById('profileImgPath');
      profileImg.src = data.data;
      alert("프로필 사진이 수정되었습니다.");
      cancelModal();
    })
    .catch(error => {
      console.error(error);
      alert('파일 업로드 중 오류가 발생하였습니다.');
    });
}

// 공통 유효성 검사
function noSpace(str) {
  return /^\S+$/.test(str);
}

// 닉네임 유효성 검사
function validateNickname(nickname) {
  return /^[a-zA-Z0-9가-힣]{1,6}$/.test(nickname);
}

// 비밀번호 유효성 검사
function validatePassword(password) {
  return /^(?=.*[a-z])(?=.*[0-9])[a-z0-9]{1,10}$/i.test(password);
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

function cancelModal() {
  closeModal();
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
        window.location.href = '/';
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