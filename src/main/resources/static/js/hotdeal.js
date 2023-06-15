// custom functions
let indexList = [];
let nowPage;
const pageSize = 20;
let customAlert = new CustomAlert();

$(document).ready(function () {
	getData(0); // TopN
	set_main_category();
	set_sign_button();
});

function search() {
	indexList = [0];
	getData(0);
}

function getUrl() {
	const type = $('#product-type').val();

	let url = '';
	if (type === 'normal') {
		url = '/products';
	} else {
		url = '/limited-products';
	}
	return url;
}

function getData(pageNumber) {
	nowPage = pageNumber;
	$.ajax({
		url: getUrl(),
		method: 'GET',
		data: {
			minPrice: $('#minPrice').val(),
			maxPrice: $('#maxPrice').val(),
			mainCategory: $('#main-category').val(),
			subCategory: $('#sub-category').val(),
			keyword: $('#keyword').val(),
			queryIndex: indexList[pageNumber],
			queryLimit: pageSize
		},
		success: function (response) {
			console.log(response)
			if (response.content.length === 0) {
				alert("검색 결과가 없습니다.")
			}
			$('#product-list').empty();
			$.each(response.content, (i, post) => {
				let temp_html =
					`<tr>
						<td>${i + 1}</td>
						<td><div onclick="detail(${post.id});" style="cursor: pointer">${post.productName}</div></td>
						<td>${post.price}</td>
					</tr>`;
				$('#product-list').append(temp_html);
			});

			// 다음 페이지 조회시 참고할 product_id 등록
			// 1페이지 조회시, 2페이지 조회에 참고할 product_id가 등록된다.
			// 이전 버튼으로 다시 0페이지로 돌아갈 경우, 1페이지 조회에 참고할 product_id는 이미 등록되어 있다.
			// 이럴 때는 등록하면 안됨.
			if (response['next'] && (indexList.length === pageNumber + 1)) {
				indexList.push(response.content[pageSize - 1].id + 1);
			}

			// '이전', '다음' 버튼 생성
			$('ul.pagination').empty();
			createPageList(response['next']);
		},
		error: function (e) {
			console.log(e);
			alert("ERROR: ", e);
			console.log("ERROR: ", e);
		},
	})
}

function createPageList(next) {
	var prevButton = '';
	var nextButton = '';

	// 첫 페이지가 아니면 '이전' 버튼 생성
	if (nowPage > 0) {
		prevButton = '<li class="page-item"><a class="page-link">이전</a></li>';
	}

	// 마지막 페이지가 아니면 '다음' 버튼 생성
	if (next) {
		nextButton = '<li class="page-item"><a class="page-link">다음</a></li>';
	}

	// 페이지 링크 반환
	var pagingLink = '';
	pagingLink = prevButton + nextButton;
	$("ul.pagination").append(pagingLink);
}

// 페이지 버튼 클릭시 함수
$(document).on("click", "ul.pagination li a", function () {
	let val = $(this).text();
	console.log('val: ' + val);

	// click on the NEXT tag
	if (val === "이전") {
		getData(nowPage - 1);
	} else if (val === "다음") {
		getData(nowPage + 1);
	}
});

function detail(id) {
	$.ajax({
		url: getUrl() + '/' + id,
		method: 'GET',
		success: function (post) {
			console.log(post);
			customAlert.detailPage(post.id, post.productName, post.categoryA, post.categoryB, post.price, post.amount)
		},
		error: function (request, status, error) {
			alert(JSON.parse(request.responseText).msg);
		},
	})
}

function buy(id) {
	if (localStorage.getItem("token") == null) {
		alert("로그인이 필요합니다.");
		cancel();
		customAlert.loginPage();
	} else {
		let quantity = $('#quantity').val();
		if (quantity <= 0 || quantity === null) {
			alert("구매 수량을 입력해주세요.")
			return;
		}
		$.ajax({
			url: getUrl() + '/' + id,
			method: 'POST',
			headers: {
				'Content-Type': 'application/json',
				'Authorization': localStorage.getItem("token")
			},
			data: JSON.stringify({
				quantity: quantity,
			}),
			success: function (response) {
				console.log(response);
				alert("구매 완료!");
				document.getElementById("alertBox").style.display = "none";
				document.getElementById("alertBackground").style.display = "none";
			},
			error: function (request, status, error) {
				alert(JSON.parse(request.responseText).msg);
			},
		});
	}
}

function cancel() {
	document.getElementById("alertBox").style.display = "none";
	document.getElementById("alertBackground").style.display = "none";
}

// 모달 창 외부 alertBackground 클릭시
$(document).on("click", "div.alertBackground", function () {
	cancel();
});

function register() {
	let adminToken = $('#adminToken').val()
	let admin = adminToken !== '';

	let username = $('#username').val();
	let password = $('#password').val();

	if (username.length === 0) {
		if (password.length === 0) {
			alert("아이디/비밀번호를 입력하세요.");
			return;
		}
		alert("아이디를 입력하세요.");
		return;
	}

	if (password.length === 0) {
		alert("비밀번호를 입력하세요.");
		return;
	}

	$.ajax({
		url: '/users/signup',
		method: 'POST',
		headers: {'Content-Type': 'application/json'},
		data: JSON.stringify({
			userId: username,
			password: password,
			admin: admin,
			adminToken: adminToken
		}),
		success: function (response) {
			alert(response.msg);
			document.getElementById("alertBox").style.display = "none";
			document.getElementById("alertBackground").style.display = "none";
		},
		error: function (request, status, error) {
			alert(JSON.parse(request.responseText).msg);
		},
	});
}

function login() {
	$.ajax({
	    url: '/users/login',
	    method: 'POST',
	    headers: {'Content-Type': 'application/json'},
	    data: JSON.stringify({
	        userId: $('#username').val(),
	        password: $('#password').val()
	    }),
	    success: function (output, status, xhr) {
			alert(output.msg);
			localStorage.setItem("token", xhr.getResponseHeader('Authorization'));
			set_sign_button();
	    },
		error: function (request, status, error) {
			alert(JSON.parse(request.responseText).msg);
		},
	})
	document.getElementById("alertBox").style.display = "none";
	document.getElementById("alertBackground").style.display = "none";
}

function logout() {
	alert("로그아웃 성공");
	localStorage.removeItem("token");
	set_sign_button();
}

function CustomAlert() {
	this.detailPage = function (productId, productName, mainCategory, subCategory, price, amount) {
		document.getElementById("alertBoxHead").innerHTML =
			'<header id="header" style="padding-top: 0px;">' +
				'<a id="categoryInfo" class="logo">' +
					mainCategory + ' > ' + subCategory + '<br>' +
					'<strong>' + productName + '</strong>' +
				'</a>' +
			'</header>';

		if ($('#product-type').val() === 'normal') {
			document.getElementById('alertBoxBody').innerHTML =
				'<table>' +
					'<tbody>' +
						'<tr><td>품번</td><td id="productInfo">' + productId + '</td></tr>' +
						'<tr><td>가격</td><td>' + price.toLocaleString('ko-KR') + '원</td></tr>' +
						'<tr><td>재고</td><td>' + amount.toLocaleString('ko-KR') + '개</td></tr>' +
						'<tr><td>구매수량</td><td><input type="number" id="quantity"></td></tr>' +
					'</tbody>' +
				'</table>'
		} else {
			document.getElementById('alertBoxBody').innerHTML =
				'<table>' +
					'<tbody>' +
						'<tr><td>품번</td><td id="productInfo">' + productId + '</td></tr>' +
						'<tr><td>가격</td><td>' + price.toLocaleString('ko-KR') + '원</td></tr>' +
						'<tr><td>재고</td><td>' + amount.toLocaleString('ko-KR') + '개</td></tr>' +
						'<tr><td>구매수량</td><td><input type="number" id="quantity" value=1 readonly></td></tr>' +
					'</tbody>' +
				'</table>'
		}

		document.getElementById('alertBoxFoot').innerHTML =
			'<div style="width: 50%; float: left;">' +
				'<button type="button" onclick="buy(' + productId + ')" style="margin-bottom: 10px;">buy</button>' +
			'</div>' +
			'<div style="width: 50%; float: right;">' +
				'<button type="button" onclick="cancel()" style="margin-bottom: 10px;">close</button>' +
			'</div>'

		this.afterEach();
	};

    this.loginPage = function () {
		document.getElementById('alertBoxHead').innerHTML =
			'<header id="header" style="padding-top: 0px;">' +
				'<a id="categoryInfo" class="logo">' +
					'<strong>Login</strong>' +
				'</a>' +
			'</header>'

		document.getElementById('alertBoxBody').innerHTML =
			'<label for="username">아이디</label>' +
			'<input id="username" type="text"/><br>' +
			'<label for="password">비밀번호</label>' +
			'<input id="password" type="text"/>'

		document.getElementById('alertBoxFoot').innerHTML =
			'<div style="width: 50%; float: left;">' +
				'<button type="button" onclick="login()" style="margin-bottom: 10px;">Login</button>' +
			'</div>' +
			'<div style="width: 50%; float: right;">' +
				'<button type="button" onclick="customAlert.registerPage()" style="margin-bottom: 10px;">Register</button>' +
			'</div>'

		this.afterEach();
    }

    this.registerPage = function () {
		document.getElementById("alertBox").style.display = "none";
        document.getElementById("alertBackground").style.display = "none";

		document.getElementById('alertBoxHead').innerHTML =
			'<header id="header" style="padding-top: 0px;">' +
				'<a id="categoryInfo" class="logo">' +
					'<strong>Regist</strong>' +
				'</a>' +
			'</header>'

		document.getElementById('alertBoxBody').innerHTML =
			'<label for="username">아이디</label>' +
			'<input id="username" type="text"/><br>' +
			'<label for="password">비밀번호</label>' +
			'<input id="password" type="text"/><br>' + 
			'<label for="adminToken">관리자 토큰</label>' +
			'<input id="adminToken" type="text"/>'

		document.getElementById('alertBoxFoot').innerHTML =
			'<button type="button" onclick="register()" style="margin-bottom: 0px;">done</button>'
			
		this.afterEach();
    }

	this.afterEach = function () {
		let dialogoverlay = document.getElementById("alertBackground");
		let dialogbox = document.getElementById("alertBox");

		let winH = window.innerHeight;
		dialogoverlay.style.height = winH + "px";

		dialogbox.style.top = "100px";

		dialogoverlay.style.display = "block";
		dialogbox.style.display = "block";

		document.getElementById("alertBoxHead").style.display = "block";
	}
}

function set_sign_button() {
	//console.log(localStorage.getItem("hiy"));
	$('#sign-button').empty();
	let loginHtml = '';
	if (localStorage.getItem("token") == null) {
		loginHtml = `<button onclick="customAlert.loginPage()" type="button" id="login-button" style="margin-top: 0px; margin-bottom: 20px;">Login</button>`
	} else {
		loginHtml = `<button onclick="logout()" type="button" id="logout-button" style="margin-top: 0px; margin-bottom: 20px;">Logout</button>`
	}
	$('#sign-button').append(loginHtml);
}

function set_main_category() {
	//$('#menu').empty();
	let mainCategories = ["상의", "아우터", "바지", "원피스",
		"스커트", "스니커즈", "신발", "가방", "여성 가방",
		"스포츠/용품", "모자", "양말/레그웨어", "속옷", "선글라스/안경테",
		"액세서리", "시계", "주얼리", "뷰티", "디지털/테크",
		"리빙", "컬처", "반려동물"];

	mainCategories.forEach((mainCategory) => {
		let value = mainCategory.replace(" ", "&nbsp")
		let temp_html = `<option value=${value}>${value}</option>`;
		$('#main-category').append(temp_html)
	});
};

function set_sub_category(mainCategory) {
	let removeBlank = mainCategory.replace(/\s/gi,"");

	$('#sub-category').empty();
	$('#sub-category').append(`<option value = "" selected>전체 선택</option>`)
	let subCategories = [];
	switch (removeBlank) {
		case "상의":
			subCategories = ["니트/스웨터", "피케/카라 티셔츠", "후드 티셔츠", "반소매 티셔츠", "맨투맨/스웨트셔츠",
				"민소매 티셔츠", "긴소매 티셔츠", "스포츠 상의", "셔츠/블라우스", "기타 상의"]
			break;
		case "아우터":
			subCategories = ["후드 집업", "환절기 코트", "블루종/MA-1", "겨울 싱글 코트", "레더/라이더스 재킷",
				"겨울 더블 코트", "무스탕/퍼", "겨울 기타 코트", "트러커 재킷", "롱패딩/롱헤비 아우터",
				"슈트/블레이저 재킷", "숏패딩/숏헤비 아우터", "카디건", "패딩 베스트", "아노락 재킷",
				"베스트", "플리스/뽀글이", "사파리/헌팅 재킷", "트레이닝 재킷", "나일론/코치 재킷",
				"스타디움 재킷", "기타 아우터"]
			break;
		case "바지":
			subCategories = ["데님 팬츠", "코튼 팬츠", "슈트 팬츠/슬랙스", "트레이닝/조거 팬츠", "숏 팬츠",
				"레깅스", "점프 슈트/오버올", "스포츠 하의", "기타 바지"]
			break;
		case "원피스":
			subCategories = ["미니 원피스", "미디 원피스", "맥시 원피스"]
			break;
		case "스커트":
			subCategories = ["미니스커트", "미디스커트", "롱스커트"]
			break;
		case "스니커즈":
			subCategories = ["캔버스/단화", "패션스니커즈화", "스포츠 스니커즈", "기타 스니커즈"]
			break;
		case "신발":
			subCategories = ["구두", "로퍼", "힐/펌프스", "플랫 슈즈", "블로퍼",
				"샌들", "슬리퍼", "기타 신발", "모카신/보트 슈즈", "부츠",
				"신발 용품"]
			break;
		case "가방":
			subCategories = ["백팩", "메신저/크로스 백", "숄더백", "토트백", "에코백",
				"보스턴/드럼/더플백", "웨이스트 백", "파우치 백", "브리프케이스", "캐리어" ,
				"가방 소품", "지갑/머니클립", "클러치 백"]
			break;
		case "여성가방":
			subCategories = ["크로스백", "토트백", "숄더백", "클러치 백", "파우치 백",
				"백팩", "웨이스트 백", "지갑/머니클립", "가방 소품"]
			break;
		case "스포츠/용품":
			subCategories = ["상의", "하의", "아우터", "스커트", "원피스",
				"상하의세트", "수영복/비치웨어", "스포츠신발", "기구/용품/장비", "스포츠가방",
				"스포츠잡화", "스포츠모자", "캠핑용품", "낚시용품"]
			break;
		case "모자":
			subCategories = ["캡/야구 모자", "헌팅캡/베레모", "페도라", "버킷/사파리햇", "비니",
				"트루퍼", "기타 모자"]
			break;
		case "양말/레그웨어":
			subCategories = ["양말", "스타킹"]
			break;
		case "속옷":
			subCategories = ["여성 속옷 상의", "여성 속옷 하의", "여성 속옷 세트", "남성 속옷", "홈웨어"]
			break;
		case "선글라스/안경테":
			subCategories = ["안경", "선글라스", "안경 소품"]
			break;
		case "액세서리":
			subCategories = ["마스크", "키링/키케이스", "벨트", "넥타이", "머플러",
				"스카프/반다나", "장갑", "기타 액세서리"]
			break;
		case "시계":
			subCategories = ["디지털", "쿼츠 아날로그", "오토매틱 아날로그", "시계 용품", "기타 시계"]
			break;
		case "주얼리":
			subCategories = ["팔찌", "반지", "목걸이/펜던트", "귀걸이", "발찌",
				"브로치/배지", "헤어 액세서리"]
			break;
		case "뷰티":
			subCategories = ["스킨케어", "클렌징", "베이스 메이크업", "포인트 메이크업", "바디케어",
				"쉐이빙/제모", "헤어케어", "향수/탈취", "뷰티 디바이스", "다이어트/헬스",
				"미용 소품", "덴탈케어"]
			break;
		case "디지털/테크":
			subCategories = ["음향가전", "휴대폰", "스마트기기", "태블릿", "TV/영상가전",
				"컴퓨터", "카메라", "생활가전", "주방가전", "계절가전",
				"차량용 디지털", "게임", "기타 디지털/테크"]
			break;
		case "리빙":
			subCategories = ["가구", "조명", "침구", "홈패브릭", "홈인테리어",
				"주방용품", "생활용품", "욕실용품", "사무용품", "기타 라이프"]
			break;
		case "컬처":
			subCategories = [ "티켓", "도서/음반", "취미", "아트", "기타 컬처"]
			break;
		case "반려동물":
			subCategories = ["반려동물 의류", "반려동물 잡화", "반려동물 용품", "반려동물 푸드"]
			break;
	}
	subCategories.forEach((subCategory) => {
		let value = subCategory.replace(" ", "&nbsp")
		let temp_html = `<option value=${value}>${value}</option>`
		$('#sub-category').append(temp_html)
	});
}