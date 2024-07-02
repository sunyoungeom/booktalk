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
      const profileImg = document.getElementById('profileImgPath');
      const nickname = document.getElementById('nickname');
      const email = document.getElementById('email');
      const signUpType = document.getElementById('signUpType');
      const signUpDate = document.getElementById('signUpDate');
console.log(data.profileImgPath)
profileImg.src = data.profileImgPath;
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

if (fieldValue === 'profileImgPath') {
  uploadImg();
  return;
  }

  if (fieldValue === 'password') {
    if (!validatePassword(newValue)) {
      alert("비밀번호는 영어와 숫자로만 구성되어야 합니다.");
      return;
    }
    data = {
      currentPassword: currentValue,
      newPassword: newValue
    };
  }

  if (fieldValue === 'nickname') {
  currentValue.textContent =
    data = {
      newNickname: newValue
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
      // 프로필 사진 수정 성공
      if (data.profileImgPath) {
        alert(data.profileImgPath);
      }
      // 닉네임 수정 성공
      if (data.nickname) {
        const nickname = document.getElementById('nickname');
        nickname.textContent = data.nickname;
      }
      // 비밀번호 수정 성공
      if (data.password) {
        alert(data.password);
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
    console.log(file)
fetch(`/api/user/${id}/profileImg`, {
    method: 'PATCH',
    body: formData  // FormData를 body로 설정하여 전송
})
.then(response => {
        if (!response.ok) {
            throw new Error('파일 업로드에 실패하였습니다.');
        }
        return response.json();
    })
    .then(data => {
        alert(data.profileImgPath); // 성공적으로 업로드된 경우 서버에서 반환하는 데이터
    })
    .catch(error => {
        console.error('파일 업로드 오류:', error);
        alert('파일 업로드 중 오류가 발생하였습니다.');
    });
//    }
/*




        if (!response.ok) {
            const result = await response.json();
            alert(result.message);
            throw new Error(result.message);
        }

        const result = await response.json();
        alert(result.profileImgPath);
    } catch (error) {
        console.error(error);
    }
*/
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