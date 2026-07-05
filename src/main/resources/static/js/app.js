const form = document.getElementById('searchForm');
const cityInput = document.getElementById('cityInput');
const statusMsg = document.getElementById('statusMsg');
const currentCard = document.getElementById('currentCard');
const forecastSection = document.getElementById('forecastSection');
const forecastRow = document.getElementById('forecastRow');

form.addEventListener('submit', async (e) => {
    e.preventDefault();
    const city = cityInput.value.trim();
    if (!city) return;

    statusMsg.textContent = '';
    currentCard.classList.add('hidden');
    forecastSection.classList.add('hidden');
    statusMsg.textContent = 'Loading...';

    try {
        const res = await fetch(`/api/weather?city=${encodeURIComponent(city)}`);
        const data = await res.json();

        if (!res.ok) {
            statusMsg.textContent = data.error || 'Something went wrong.';
            return;
        }

        statusMsg.textContent = '';
        renderCurrent(data);
        renderForecast(data.forecast);

    } catch (err) {
        statusMsg.textContent = 'Could not reach the server. Is the app running?';
        console.error(err);
    }
});

function renderCurrent(data) {
    document.getElementById('placeName').textContent = `${data.city}, ${data.country}`;
    document.getElementById('tempVal').textContent = Math.round(data.currentTemp);
    document.getElementById('descVal').textContent = data.description;
    document.getElementById('feelsVal').textContent = `${Math.round(data.feelsLike)}°C`;
    document.getElementById('humidityVal').textContent = `${data.humidity}%`;
    document.getElementById('windVal').textContent = `${data.windSpeed} m/s`;
    document.getElementById('iconImg').src = `https://openweathermap.org/img/wn/${data.icon}@2x.png`;

    currentCard.classList.remove('hidden');
}

function renderForecast(forecast) {
    forecastRow.innerHTML = '';

    forecast.forEach(day => {
        const dateObj = new Date(day.date);
        const label = dateObj.toLocaleDateString('en-US', { weekday: 'short', day: 'numeric' });

        const card = document.createElement('div');
        card.className = 'forecast-day';
        card.innerHTML = `
            <p class="fd-date">${label}</p>
            <img src="https://openweathermap.org/img/wn/${day.icon}.png" alt="${day.description}">
            <p class="fd-temps">${Math.round(day.maxTemp)}° <span class="lo">${Math.round(day.minTemp)}°</span></p>
            <p class="fd-humidity">Humidity: ${day.humidity}%</p>
        `;
        forecastRow.appendChild(card);
    });

    forecastSection.classList.remove('hidden');
}
