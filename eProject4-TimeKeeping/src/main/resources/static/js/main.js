(function ($) {
    "use strict";

    // Spinner
    var spinner = function () {
        setTimeout(function () {
            if ($('#spinner').length > 0) {
                $('#spinner').removeClass('show');
            }
        }, 1);
    };
    spinner();
    
    
    // Back to top button
    $(window).scroll(function () {
        if ($(this).scrollTop() > 300) {
            $('.back-to-top').fadeIn('slow');
        } else {
            $('.back-to-top').fadeOut('slow');
        }
    });
    $('.back-to-top').click(function () {
        $('html, body').animate({scrollTop: 0}, 1500, 'easeInOutExpo');
        return false;
    });


    // Sidebar Toggler
    $('.sidebar-toggler').click(function () {
        $('.sidebar, .content').toggleClass("open");
        return false;
    });


    // Progress Bar
    $('.pg-bar').waypoint(function () {
        $('.progress .progress-bar').each(function () {
            $(this).css("width", $(this).attr("aria-valuenow") + '%');
        });
    }, {offset: '80%'});


    // Calender
    $('#calender').datetimepicker({
        inline: true,
        format: 'L'
    });


    // Testimonials carousel
    $(".testimonial-carousel").owlCarousel({
        autoplay: true,
        smartSpeed: 1000,
        items: 1,
        dots: true,
        loop: true,
        nav : false
    });
// Biểu đồ lương (sử dụng dữ liệu thật từ API)
    $.ajax({
        url: "/api/salary/monthlyYearly",
        method: "GET",
        success: function (data) {
            var currentYear = new Date().getFullYear();
            var labels = [];
            var salaryData = new Array(12).fill(0); // Khởi tạo mảng với 12 phần tử bằng 0

            // Tạo nhãn cho 12 tháng của năm hiện tại
            for (var i = 1; i <= 12; i++) {
                labels.push("Tháng " + i + " - " + currentYear);
            }

            // Duyệt qua dữ liệu trả về từ API và điền vào mảng salaryData
            data.forEach(function (item) {
                var year = item[0];
                var month = item[1];
                var salary = item[2];

                // Nếu năm từ API trùng với năm hiện tại thì cập nhật dữ liệu lương
                if (year === currentYear) {
                    salaryData[month - 1] = salary; // Cập nhật lương vào đúng tháng (chỉ số bắt đầu từ 0)
                }
            });

            // Khởi tạo biểu đồ với 12 tháng
            var ctxSalary = $("#salary-chart").get(0).getContext("2d");
            var salaryChart = new Chart(ctxSalary, {
                type: "bar",
                data: {
                    labels: labels,  // Nhãn các tháng
                    datasets: [{
                        label: "Tổng lương",
                        data: salaryData,  // Dữ liệu lương từ API
                        backgroundColor: "rgba(0, 156, 255, .7)"
                    }]
                },
                options: {
                    responsive: true,
                    scales: {
                        y: {
                            beginAtZero: true,
                            title: {
                                display: true,
                                text: 'Số tiền (VND)'
                            }
                        },
                        x: {
                            title: {
                                display: true,
                                text: 'Tháng'
                            }
                        }
                    }
                }
            });
        }
    });

    var currentYear = new Date().getFullYear();
    var years = [];
    for (var i = currentYear; i <= 2030; i++) {
        years.push(i);
    }

// Biểu đồ nhân sự (sử dụng dữ liệu thật từ API)
    $.ajax({
        url: "/api/employees/yearly",
        method: "GET",
        success: function (data) {
            var ctxEmployee = $("#employee-chart").get(0).getContext("2d");
            var employeeChart = new Chart(ctxEmployee, {
                type: "line",
                data: {
                    labels: years, // Các năm từ năm hiện tại tới 2030
                    datasets: [{
                        label: "Số lượng nhân sự",
                        data: data, // Dữ liệu số lượng nhân sự từ API
                        backgroundColor: "rgba(0, 156, 255, .5)",
                        borderColor: "rgba(0, 156, 255, 1)",
                        fill: true
                    }]
                },
                options: {
                    responsive: true,
                    scales: {
                        y: {
                            beginAtZero: true,
                            title: {
                                display: true,
                                text: 'Số lượng nhân sự'
                            }
                        },
                        x: {
                            title: {
                                display: true,
                                text: 'Năm'
                            }
                        }
                    }
                }
            });
        }
    });





})(jQuery);

