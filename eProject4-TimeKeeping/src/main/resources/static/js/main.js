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
    });// Salary Chart (using real data from API)
    $.ajax({
        url: "/api/salary/monthlyYearly",
        method: "GET",
        success: function (data) {
            var currentYear = new Date().getFullYear();
            var labels = [];
            var salaryData = new Array(12).fill(0); // Initialize array with 12 elements as 0

            // Create labels for 12 months of the current year
            for (var i = 1; i <= 12; i++) {
                labels.push("Month " + i + " - " + currentYear);
            }

            // Loop through the data returned from the API and fill the salaryData array
            data.forEach(function (item) {
                var year = item[0];
                var month = item[1];
                var salary = item[2];

                // If the year from the API matches the current year, update the salary data
                if (year === currentYear) {
                    salaryData[month - 1] = salary; // Update the correct month (index starts at 0)
                }
            });

            // Initialize the chart with 12 months
            var ctxSalary = $("#salary-chart").get(0).getContext("2d");
            var salaryChart = new Chart(ctxSalary, {
                type: "bar",
                data: {
                    labels: labels,  // Month labels
                    datasets: [{
                        label: "Total Salary",
                        data: salaryData,  // Salary data from API
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
                                text: 'Amount (VND)'
                            }
                        },
                        x: {
                            title: {
                                display: true,
                                text: 'Month'
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

// Employee Growth Chart (using real data from API)
    $.ajax({
        url: "/api/employees/yearly",
        method: "GET",
        success: function (data) {
            var ctxEmployee = $("#employee-chart").get(0).getContext("2d");
            var employeeChart = new Chart(ctxEmployee, {
                type: "line",
                data: {
                    labels: years, // Years from the current year to 2030
                    datasets: [{
                        label: "Employee Count",
                        data: data, // Employee count data from API
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
                                text: 'Employee Count'
                            }
                        },
                        x: {
                            title: {
                                display: true,
                                text: 'Year'
                            }
                        }
                    }
                }
            });
        }
    });

})(jQuery);
