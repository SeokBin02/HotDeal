/*
	Editorial by HTML5 UP
	html5up.net | @ajlkn
	Free for personal and commercial use under the CCA 3.0 license (html5up.net/license)
*/

(function($) {

	var	$window = $(window),
		$head = $('head'),
		$body = $('body');

	// Breakpoints.
		breakpoints({
			xlarge:   [ '1281px',  '1680px' ],
			large:    [ '981px',   '1280px' ],
			medium:   [ '737px',   '980px'  ],
			small:    [ '481px',   '736px'  ],
			xsmall:   [ '361px',   '480px'  ],
			xxsmall:  [ null,      '360px'  ],
			'xlarge-to-max':    '(min-width: 1681px)',
			'small-to-xlarge':  '(min-width: 481px) and (max-width: 1680px)'
		});

	// Stops animations/transitions until the page has ...

		// ... loaded.
			$window.on('load', function() {
				window.setTimeout(function() {
					$body.removeClass('is-preload');
				}, 100);
			});

		// ... stopped resizing.
			var resizeTimeout;

			$window.on('resize', function() {

				// Mark as resizing.
					$body.addClass('is-resizing');

				// Unmark after delay.
					clearTimeout(resizeTimeout);

					resizeTimeout = setTimeout(function() {
						$body.removeClass('is-resizing');
					}, 100);

			});

	// Fixes.

		// Object fit images.
			if (!browser.canUse('object-fit')
			||	browser.name == 'safari')
				$('.image.object').each(function() {

					var $this = $(this),
						$img = $this.children('img');

					// Hide original image.
						$img.css('opacity', '0');

					// Set background.
						$this
							.css('background-image', 'url("' + $img.attr('src') + '")')
							.css('background-size', $img.css('object-fit') ? $img.css('object-fit') : 'cover')
							.css('background-position', $img.css('object-position') ? $img.css('object-position') : 'center');

				});

	// Sidebar.
		var $sidebar = $('#sidebar'),
			$sidebar_inner = $sidebar.children('.inner');

		// Inactive by default on <= large.
			breakpoints.on('<=large', function() {
				$sidebar.addClass('inactive');
			});

			breakpoints.on('>large', function() {
				$sidebar.removeClass('inactive');
			});

		// Hack: Workaround for Chrome/Android scrollbar position bug.
			if (browser.os == 'android'
			&&	browser.name == 'chrome')
				$('<style>#sidebar .inner::-webkit-scrollbar { display: none; }</style>')
					.appendTo($head);

		// Toggle.
			$('<a href="#sidebar" class="toggle">Toggle</a>')
				.appendTo($sidebar)
				.on('click', function(event) {

					// Prevent default.
						event.preventDefault();
						event.stopPropagation();

					// Toggle.
						$sidebar.toggleClass('inactive');

				});

		// Events.

			// Link clicks.
				$sidebar.on('click', 'a', function(event) {

					// >large? Bail.
						if (breakpoints.active('>large'))
							return;

					// Vars.
						var $a = $(this),
							href = $a.attr('href'),
							target = $a.attr('target');

					// Prevent default.
						event.preventDefault();
						event.stopPropagation();

					// Check URL.
						if (!href || href == '#' || href == '')
							return;

					// Hide sidebar.
						$sidebar.addClass('inactive');

					// Redirect to href.
						setTimeout(function() {

							if (target == '_blank')
								window.open(href);
							else
								window.location.href = href;

						}, 500);

				});

			// Prevent certain events inside the panel from bubbling.
				$sidebar.on('click touchend touchstart touchmove', function(event) {

					// >large? Bail.
						if (breakpoints.active('>large'))
							return;

					// Prevent propagation.
						event.stopPropagation();

				});

			// Hide panel on body click/tap.
				$body.on('click touchend', function(event) {

					// >large? Bail.
						if (breakpoints.active('>large'))
							return;

					// Deactivate.
						$sidebar.addClass('inactive');

				});

		// Scroll lock.
		// Note: If you do anything to change the height of the sidebar's content, be sure to
		// trigger 'resize.sidebar-lock' on $window so stuff doesn't get out of sync.

			$window.on('load.sidebar-lock', function() {

				var sh, wh, st;

				// Reset scroll position to 0 if it's 1.
					if ($window.scrollTop() == 1)
						$window.scrollTop(0);

				$window
					.on('scroll.sidebar-lock', function() {

						var x, y;

						// <=large? Bail.
							if (breakpoints.active('<=large')) {

								$sidebar_inner
									.data('locked', 0)
									.css('position', '')
									.css('top', '');

								return;

							}

						// Calculate positions.
							x = Math.max(sh - wh, 0);
							y = Math.max(0, $window.scrollTop() - x);

						// Lock/unlock.
							if ($sidebar_inner.data('locked') == 1) {

								if (y <= 0)
									$sidebar_inner
										.data('locked', 0)
										.css('position', '')
										.css('top', '');
								else
									$sidebar_inner
										.css('top', -1 * x);

							}
							else {

								if (y > 0)
									$sidebar_inner
										.data('locked', 1)
										.css('position', 'fixed')
										.css('top', -1 * x);

							}

					})
					.on('resize.sidebar-lock', function() {

						// Calculate heights.
							wh = $window.height();
							sh = $sidebar_inner.outerHeight() + 30;

						// Trigger scroll.
							$window.trigger('scroll.sidebar-lock');

					})
					.trigger('resize.sidebar-lock');

				});

	// Menu.
		var $menu = $('#menu'),
			$menu_openers = $menu.children('ul').find('.opener');

		// Openers.
			$menu_openers.each(function() {

				var $this = $(this);

				$this.on('click', function(event) {

					// Prevent default.
						event.preventDefault();

					// Toggle.
						$menu_openers.not($this).removeClass('active');
						$this.toggleClass('active');

					// Trigger resize (sidebar lock).
						$window.triggerHandler('resize.sidebar-lock');

				});

			});

})(jQuery);

// custom functions
$(document).ready(function () {
	//getData(0); // TopN
	set_main_category();
});

function getData(startPage) {
	$.ajax({
		url: '/products',
		method: 'GET',
		data: {
			minPrice: $('#minPrice').val(),
			maxPrice: $('#maxPrice').val(),
			mainCategory: $('#main-category').val(),
			subCategory: $('#sub-category').val(),
			keyword: $('#keyword').val(),
			page: startPage,
			size: 20
		},
		success: function (response) {
			console.log(response)
			$('#product-list').empty();
			$.each(response.content, (i, post) => {
				let temp_html = `<tr>
							<td>${i + 1}</td>
							<td>${post.productName}</td>
							<td>${post.price}</td>
						</tr>`;
				$('#product-list').append(temp_html);
			});

			if ($('ul.pagination li').length - 2 != response.totalPages) {
				// 처음 불러왔을 때 페이지 목록 구성
				$('ul.pagination').empty();
				createPageList(response);
			}
		},
		error: function (e) {
			alert("ERROR: ", e);
			console.log("ERROR: ", e);
		},
	})
}

function createPageList(response) {
	totalPages = response.totalPages;

	var pageNumber = response.pageable.pageNumber;

	var numLinks = 10;

	// 현재 1페이지가 아닐 경우, '이전' 버튼 생성
	var first = '';
	var prev = '';
	if (pageNumber > 0) {
		if (pageNumber !== 0) {
			first = '<li class="page-item"><a class="page-link">&nbsp;« 처음&nbsp;</a></li>';
		}
		prev = '<li class="page-item"><a class="page-link">&nbsp;‹ 이전&nbsp;</a></li>';
	} else {
		prev = ''; // 1페이지에 있으면 '이전' 버튼 비활성화
		first = ''; // 1페이지에 있으면 '처음' 버튼 비활성화
	}

	// 마지막 페이지가 아닐 경우, '다음' 버튼 생성
	var next = '';
	var last = '';
	if (pageNumber < totalPages) {
		if (pageNumber !== totalPages - 1) {
			next = '<li class="page-item"><a class="page-link">&nbsp;&nbsp;다음 ›</a></li>';
			last = '<li class="page-item"><a class="page-link">&nbsp;끝 »&nbsp;</a></li>';
		}
	} else {
		next = ''; // 마지막 페이지에 있으면 '다음' 버튼 비활성화
		last = ''; // 마지막 페이지에 있으면 '끝' 버튼 비활성화
	}

	var start = pageNumber - (pageNumber % numLinks) + 1;
	var end = start + numLinks - 1;
	end = Math.min(totalPages, end);
	var pagingLink = '';

	for (var i = start; i <= end; i++) {
		if (i == pageNumber + 1) {
			// 현재 페이지 버튼은 만들지 않음
			pagingLink += '<li class="page-item active"><a class="page-link"> ' + i + ' </a></li>';
		} else {
			pagingLink += '<li class="page-item"><a class="page-link"> ' + i + ' </a></li>';
		}
	}

	// 페이지 링크 반환
	pagingLink = first + prev + pagingLink + next + last;
	console.log(pagingLink);
	$("ul.pagination").append(pagingLink);
}

// 페이지 버튼 클릭시 함수
$(document).on("click", "ul.pagination li a", function () {
	var data = $(this).attr('data');
	let val = $(this).text();
	console.log('val: ' + val);

	// click on the NEXT tag
	if(val === "« 처음") {
		let currentActive = $("li.active");
		getData(0);
		$("li.active").removeClass("active"); // 이전 버튼 비활성화
		currentActive.next().addClass("active"); // '처음' 버튼 활성화
	} else if(val === "끝 »") {
		getData(totalPages - 1);
		$("li.active").removeClass("active"); // 이전 버튼 비활성화
		currentActive.next().addClass("active"); // '끝' 버튼 활성화
	} else if(val === "다음 ›") {
		let activeValue = parseInt($("ul.pagination li.active").text());
		if(activeValue < totalPages){
			let currentActive = $("li.active");
			startPage = activeValue;
			getData(startPage);
			$("li.active").removeClass("active"); // 이전 버튼 비활성화
			currentActive.next().addClass("active"); // '다음' 버튼 활성화
		}
	} else if(val === "‹ 이전") {
		let activeValue = parseInt($("ul.pagination li.active").text());
		if(activeValue > 1) {
			startPage = activeValue - 2; // 이전 페이지 번호
			getData(startPage);
			let currentActive = $("li.active");
			currentActive.removeClass("active"); // 이전 버튼 비활성화
			currentActive.prev().addClass("active"); // '이전' 버튼 활성화
		}
	} else { // "번호"
		startPage = parseInt(val - 1);
		getData(startPage);
		$("li.active").removeClass("active"); // 이전 버튼 비활성화
		$(this).parent().addClass("active"); // 'N' 버튼 활성화
	}
});

function set_product() {
	$('#product-list').empty();
	for (var i = 1; i <= 20; i++) {
		let temp_html = `
		<tr>
			<td>${i}</td>
			<td>상품명입니다.</td>
			<td>99999</td>
		</tr>`
		$('#product-list').append(temp_html);
	}
}

function set_main_category() {
	$('#menu').empty();
	let mainCategories = ["상의", "아우터", "바지", "원피스",
                "스커트", "스니커즈", "신발", "가방", "여성 가방",
                "스포츠/용품", "모자", "양말/레그웨어", "속옷", "선글라스/안경테",
                "액세서리", "시계", "주얼리", "뷰티", "디지털/테크",
                "리빙", "컬처", "반려동물"];

	mainCategories.forEach((mainCategory) => {
		let temp_html = `<option value=${mainCategory.replace(" ", "&nbsp")}>${mainCategory}</option>`;
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
		let temp_html = `<option value=${subCategory.replace(" ", "&nbsp")}>${subCategory.replace(" ", "&nbsp")}</option>`
		$('#sub-category').append(temp_html)
	});
}