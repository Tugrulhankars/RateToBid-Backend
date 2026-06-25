let stompClient = null;
let connected = false;

// WebSocket bağlantısı
function connectWebSocket() {
    const token = getToken();
    console.log('connectWebSocket çağrısı: token=', token);
    if (!token) {
        document.getElementById('biddingForm').style.display = 'none';
        document.getElementById('notLoggedIn').style.display = 'block';
        return;
    }

    const socket = new SockJS('/ws-race?token=' + token);
    stompClient = Stomp.over(socket);
    
    stompClient.connect(
        {
            'Authorization': 'Bearer ' + token
        },
        function(frame) {
            connected = true;
            console.log('WebSocket bağlandı:', frame);
            
            // Açık artırma kanalına abone ol
            stompClient.subscribe('/topic/auction/' + auctionId, function(message) {
                const bidResponse = JSON.parse(message.body);
                console.log('WS SUBSCRIBE güncellendi /topic/auction/' + auctionId + ':', bidResponse);
                updateBidDisplay(bidResponse);
            });
            
            // Hata mesajlarını dinle
            stompClient.subscribe('/user/queue/errors', function(message) {
                const error = JSON.parse(message.body);
                console.error('BID ERROR (WS):', error);
                showBidError(error.message);
            });
        },
        function(error) {
            console.error('WebSocket bağlantı hatası:', error);
            connected = false;
        }
    );
}

// Teklif gönderme
function placeBid() {
    if (!connected) {
        showBidError('WebSocket bağlantısı yok. Lütfen sayfayı yenileyin.');
        return;
    }

    const bidAmount = parseFloat(document.getElementById('bidAmount').value);
    const errorDiv = document.getElementById('bidError');
    
    errorDiv.classList.remove('show');
    errorDiv.textContent = '';

    if (!bidAmount || bidAmount <= minBid) {
        console.error('Hatalı teklif amount:', bidAmount, 'min:', minBid);
        showBidError('Teklif miktarı minimum ' + minBid.toFixed(2) + ' ₺ olmalıdır.');
        return;
    }

    const bidRequest = {
        auctionId: auctionId,
        auctionItemId: auctionId,
        amount: bidAmount
    };
    console.log('WebSocket SEND /app/bid:', bidRequest);
    stompClient.send('/app/bid', {}, JSON.stringify(bidRequest));
    document.getElementById('bidAmount').value = '';
}

// Teklif gösterimini güncelle
function updateBidDisplay(bidResponse) {
    // Mevcut fiyatı güncelle
    document.getElementById('currentPrice').textContent = bidResponse.currentPrice.toFixed(2) + ' ₺';
    
    // Minimum teklifi güncelle
    document.getElementById('minBid').textContent = bidResponse.currentPrice.toFixed(2) + ' ₺';
    
    // Teklif listesine ekle
    addBidToList(bidResponse);
    
    // Animasyon efekti
    const priceElement = document.getElementById('currentPrice');
    priceElement.style.animation = 'pulse 0.5s';
    setTimeout(() => {
        priceElement.style.animation = '';
    }, 500);
}

// Teklif listesine ekle
function addBidToList(bidResponse) {
    const bidsList = document.getElementById('bidsList');
    
    // İlk teklif ise boş mesajı kaldır
    if (bidsList.querySelector('.text-muted')) {
        bidsList.innerHTML = '';
    }
    
    const bidItem = document.createElement('div');
    bidItem.className = 'bid-item';
    bidItem.innerHTML = `
        <div>
            <div class="bid-user">${bidResponse.username}</div>
            <div class="bid-time">${new Date().toLocaleString('tr-TR')}</div>
        </div>
        <div class="bid-amount">${bidResponse.currentPrice.toFixed(2)} ₺</div>
    `;
    
    bidsList.insertBefore(bidItem, bidsList.firstChild);
}

// Hata göster
function showBidError(message) {
    const errorDiv = document.getElementById('bidError');
    errorDiv.textContent = message;
    errorDiv.classList.add('show');
}

// Sayfa yüklendiğinde
document.addEventListener('DOMContentLoaded', function() {
    // Teklif butonu
    const placeBidBtn = document.getElementById('placeBidBtn');
    if (placeBidBtn) {
        placeBidBtn.addEventListener('click', placeBid);
    }
    
    // Enter tuşu ile teklif verme
    const bidAmountInput = document.getElementById('bidAmount');
    if (bidAmountInput) {
        bidAmountInput.addEventListener('keypress', function(e) {
            if (e.key === 'Enter') {
                placeBid();
            }
        });
    }
    
    // WebSocket bağlantısını kur
    connectWebSocket();
    
    // Sayfa kapatılırken bağlantıyı kapat
    window.addEventListener('beforeunload', function() {
        if (stompClient && connected) {
            stompClient.disconnect();
        }
    });
});

// CSS animasyonu ekle
const style = document.createElement('style');
style.textContent = `
    @keyframes pulse {
        0% { transform: scale(1); }
        50% { transform: scale(1.1); color: #4caf50; }
        100% { transform: scale(1); }
    }
`;
document.head.appendChild(style);

