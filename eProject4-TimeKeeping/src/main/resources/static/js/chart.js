$(document).ready(function () {
    $.ajax({
        url: "/jobs/monthly-count",  // Ensure this URL is correct
        method: "GET",
        success: function (data) {
            // Log the data to check if it's an array
            console.log(data);

            // Check if the data is an array
            if (Array.isArray(data)) {
                var months = [];
                var jobCounts = [];

                // Process the data assuming it's an array
                data.forEach(function (item) {
                    months.push("Month " + item.month);  // e.g., "Month 1" for January
                    jobCounts.push(item.count);  // Job count for each month
                });

                // Create the chart
                var ctx = document.getElementById("jobCountChart").getContext("2d");
                var jobCountChart = new Chart(ctx, {
                    type: 'bar',
                    data: {
                        labels: months,
                        datasets: [{
                            label: 'Number of Jobs',
                            data: jobCounts,
                            backgroundColor: 'rgba(75, 192, 192, 0.2)',
                            borderColor: 'rgba(75, 192, 192, 1)',
                            borderWidth: 1
                        }]
                    },
                    options: {
                        responsive: true,
                        scales: {
                            y: {
                                beginAtZero: true,
                                title: {
                                    display: true,
                                    text: 'Number of Jobs'
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
            } else {
                console.error("API response is not an array:", data);
            }
        },
        error: function (error) {
            console.error("Error fetching job count data:", error);
        }
    });
});
