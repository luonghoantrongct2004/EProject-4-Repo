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
    $(document).ready(function () {
        var currentYear = new Date().getFullYear();
        var years = [];
        for (var i = currentYear; i <= 2030; i++) {
            years.push(i);
        }

        // Populate year dropdowns
        years.forEach(function (year) {
            $('#salaryYearSelect').append(`<option value="${year}">${year}</option>`);
            $('#employeeYearSelect').append(`<option value="${year}">${year}</option>`);
        });

        // Set the default selected year for both dropdowns to the current year
        $('#salaryYearSelect').val(currentYear);
        $('#employeeYearSelect').val(currentYear);

        // Variables to store the chart instances
        var salaryChartInstance = null;
        var employeeChartInstance = null;

        // Load the default charts for the current year when the page loads
        getSalaryData(currentYear, 0);  // 0 for "All Months"
        getEmployeeData(currentYear);

        // Salary Chart Filter
        $('#filterSalary').on('click', function () {
            var selectedYear = $('#salaryYearSelect').val();
            var selectedMonth = $('#salaryMonthSelect').val();
            getSalaryData(selectedYear, selectedMonth);
        });

        // Employee Growth Chart Filter
        $('#filterEmployee').on('click', function () {
            var selectedYear = $('#employeeYearSelect').val();
            getEmployeeData(selectedYear);
        });

        function getSalaryData(year, month) {
            $.ajax({
                url: `/api/salary/monthlyYearly?year=${year}&month=${month}`,
                method: "GET",
                success: function (data) {
                    var salaryData = new Array(12).fill(0); // Initialize array with 12 elements as 0

                    // Populate the salaryData array with the results from the API
                    data.forEach(function (item) {
                        var apiYear = item[0];
                        var apiMonth = item[1];
                        var salary = item[2];

                        if (apiYear == year) {
                            salaryData[apiMonth - 1] = salary;
                        }
                    });

                    var labels = [];
                    for (var i = 1; i <= 12; i++) {
                        labels.push("Month " + i + " - " + year);
                    }

                    // Check if there is an existing chart instance and destroy it before creating a new one
                    if (salaryChartInstance !== null) {
                        salaryChartInstance.destroy();
                    }

                    var ctxSalary = $("#salary-chart").get(0).getContext("2d");
                    salaryChartInstance = new Chart(ctxSalary, {
                        type: "bar",
                        data: {
                            labels: labels,
                            datasets: [{
                                label: "Total Salary",
                                data: salaryData,
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
        }

        function getEmployeeData(year) {
            $.ajax({
                url: `/api/employees/yearly?startYear=${year}&endYear=${year}`,
                method: "GET",
                success: function (data) {
                    // Check if there is an existing chart instance and destroy it before creating a new one
                    if (employeeChartInstance !== null) {
                        employeeChartInstance.destroy();
                    }

                    var ctxEmployee = $("#employee-chart").get(0).getContext("2d");
                    employeeChartInstance = new Chart(ctxEmployee, {
                        type: "line",
                        data: {
                            labels: [year],
                            datasets: [{
                                label: "Employee Count",
                                data: data,
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
        }
    });


})(jQuery);
