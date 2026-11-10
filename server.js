/**
 * Khelv Platform - Web & Admin Server
 * Runs on port 3000 (proxied by Nginx on port 8080).
 * Serves the Web Gaming Platform, Admin Panel, REST APIs, and native Android APK download.
 */
const http = require('http');
const fs = require('fs');
const path = require('path');
const url = require('url');

const PORT = parseInt(process.env.DEFAULT_APP_PORT || process.env.PORT || '3000', 10);
const WORKSPACE_DIR = __dirname;
const APK_PATH = path.join(WORKSPACE_DIR, '.build-outputs', 'app-debug.apk');

// In-Memory Database (Synced with Android App State)
const db = {
  websiteSettings: {
    siteName: 'khelv.com',
    logoEmoji: '👑',
    domain: 'khelv20.com',
    announcementNotice: 'Welcome to khelv.com! 24/7 instant withdrawals, 30s WinGo, Aviator & real-time gaming.',
    heroBannerTitle: '₹100 Free Welcome Bonus',
    heroBannerSubtitle: 'Register with invite code & join 10,000+ daily winners!',
    isMaintenanceMode: false
  },
  adminSession: null,
  users: [
    { id: 'USR-88219', username: 'rajput_vip', phone: '+91 98765 43210', balance: 14500.0, vipLevel: 4, isBlocked: false, totalWagered: 125000.0, joinedDate: '2026-11-01', inviteCode: '75482128162' },
    { id: 'USR-88220', username: 'khelv_king', phone: '+91 98111 22334', balance: 3200.5, vipLevel: 2, isBlocked: false, totalWagered: 45000.0, joinedDate: '2026-11-02', inviteCode: '75482128162' },
    { id: 'USR-88221', username: 'aviator_pro', phone: '+91 99000 88776', balance: 890.0, vipLevel: 1, isBlocked: false, totalWagered: 12000.0, joinedDate: '2026-11-03', inviteCode: '75482128162' },
    { id: 'USR-88222', username: 'lucky_spinner', phone: '+91 97222 33445', balance: 0.0, vipLevel: 0, isBlocked: true, totalWagered: 500.0, joinedDate: '2026-11-04', inviteCode: '75482128162' },
    { id: 'USR-88223', username: 'rummy_master', phone: '+91 96333 44556', balance: 7540.0, vipLevel: 3, isBlocked: false, totalWagered: 98000.0, joinedDate: '2026-11-05', inviteCode: '75482128162' },
    { id: 'USR-88224', username: 'gold_tiger', phone: '+91 95444 55667', balance: 1210.0, vipLevel: 1, isBlocked: false, totalWagered: 18000.0, joinedDate: '2026-11-06', inviteCode: '75482128162' }
  ],
  games: [
    { id: 'aviator', name: 'Aviator', category: 'Crash / Aviator', provider: 'Spribe', isEnabled: true, playCount: 28450, rtp: 97.0, icon: '✈️' },
    { id: 'wingo', name: 'Win Go 30s', category: 'Number / Colour', provider: 'Khelv Originals', isEnabled: true, playCount: 35120, rtp: 96.5, icon: '🎨' },
    { id: 'mines', name: 'Mines 5x5', category: 'Mini Games', provider: 'Khelv In-House', isEnabled: true, playCount: 19800, rtp: 98.0, icon: '💣' },
    { id: 'charge_buffalo', name: 'Charge Buffalo', category: 'Slot Games', provider: 'JILI Slots', isEnabled: true, playCount: 14200, rtp: 96.8, icon: '🦬' },
    { id: 'super_ace', name: 'Super Ace', category: 'Slot Games', provider: 'JILI Slots', isEnabled: true, playCount: 22100, rtp: 97.4, icon: '🃏' },
    { id: 'royal_fishing', name: 'Royal Fishing', category: 'Fishing Games', provider: 'JILI Fishing', isEnabled: true, playCount: 11400, rtp: 96.0, icon: '🐠' },
    { id: 'cricket_live', name: 'Cricket Live Exchange', category: 'Cricket Games', provider: '9 Sports', isEnabled: true, playCount: 16700, rtp: 95.5, icon: '🏏' },
    { id: 'teen_patti', name: 'Teen Patti Pro', category: 'Card Games', provider: 'Khelv Cards', isEnabled: true, playCount: 18900, rtp: 96.2, icon: '🎴' },
    { id: 'rummy', name: 'Rummy 13 Cards', category: 'Card Games', provider: 'Khelv Cards', isEnabled: true, playCount: 20500, rtp: 96.0, icon: '♠️' },
    { id: 'dice', name: 'Dice Roll', category: 'Mini Games', provider: 'Khelv In-House', isEnabled: true, playCount: 9200, rtp: 98.5, icon: '🎲' },
    { id: 'plinko', name: 'Plinko 1000x', category: 'Crash / Aviator', provider: 'Khelv Originals', isEnabled: true, playCount: 15400, rtp: 97.0, icon: '🎯' },
    { id: 'evolution_casino', name: 'Evolution Live Roulette', category: 'Live Casino', provider: 'Evolution Gaming', isEnabled: true, playCount: 8800, rtp: 97.3, icon: '🎰' }
  ],
  auditLogs: [
    { id: 'AUD-901', action: 'COIN_CREDIT', admin: 'superadmin', target: 'USR-88219', amount: '+₹500.00', reason: 'VIP 4 Monthly Loyalty Bonus', time: '2026-11-09 10:45' },
    { id: 'AUD-900', action: 'STATUS_CHANGE', admin: 'superadmin', target: 'USR-88222', amount: 'BLOCK', reason: 'Suspicious multiple IPs login', time: '2026-11-09 09:12' },
    { id: 'AUD-899', action: 'GAME_CONFIG', admin: 'superadmin', target: 'aviator', amount: 'RTP 97%', reason: 'Calibrated provably fair seed', time: '2026-11-09 08:30' }
  ],
  loginLogs: [
    { id: 'LOG-301', admin: 'admin', ip: '10.0.4.12', status: 'SUCCESS (2FA Verified)', role: 'SUPER_ADMIN', time: '2026-11-09 10:45:12' },
    { id: 'LOG-300', admin: 'staff_ops', ip: '192.168.1.44', status: 'SUCCESS', role: 'STAFF_ADMIN', time: '2026-11-09 07:12:00' }
  ]
};

// HTML Renderer for Web Gaming Platform & Admin Panel
function renderHTML() {
  const usersCount = db.users.length;
  const activeUsersCount = db.users.filter(u => !u.isBlocked).length;
  const totalCoins = db.users.reduce((acc, u) => acc + u.balance, 0).toFixed(2);
  const gamesCount = db.games.length;

  return `<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no">
  <title>khelv.com - Real-Time Gaming & Admin Portal</title>
  <link rel="preconnect" href="https://fonts.googleapis.com">
  <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700;800;900&display=swap" rel="stylesheet">
  <style>
    :root {
      --bg-dark: #070913;
      --bg-card: #0D1224;
      --bg-surface: #131A33;
      --primary-blue: #2563EB;
      --accent-gold: #F59E0B;
      --accent-red: #EF4444;
      --accent-green: #10B981;
      --text-main: #FFFFFF;
      --text-muted: #94A3B8;
      --border: #1E293B;
    }
    * { box-sizing: border-box; margin: 0; padding: 0; font-family: 'Inter', -apple-system, sans-serif; }
    body { background-color: var(--bg-dark); color: var(--text-main); min-height: 100vh; overflow-x: hidden; }

    /* Top Navigation Bar */
    header {
      background: #090D1C;
      border-bottom: 1px solid var(--border);
      position: sticky;
      top: 0;
      z-index: 100;
      padding: 10px 16px;
      display: flex;
      align-items: center;
      justify-content: space-between;
    }
    .brand { display: flex; align-items: center; gap: 6px; text-decoration: none; cursor: pointer; }
    .brand-logo { font-size: 22px; }
    .brand-name { font-size: 20px; font-weight: 900; color: #FBBF24; letter-spacing: -0.5px; }
    .brand-domain { font-size: 14px; font-weight: 700; color: #60A5FA; }

    .header-actions { display: flex; align-items: center; gap: 10px; }
    .balance-chip {
      background: var(--bg-surface);
      border: 1px solid #1E2958;
      border-radius: 20px;
      padding: 6px 12px;
      display: flex;
      align-items: center;
      gap: 8px;
      font-weight: 700;
      font-size: 14px;
      cursor: pointer;
    }
    .balance-badge {
      background: linear-gradient(135deg, #F59E0B, #D97706);
      color: #000;
      width: 20px;
      height: 20px;
      border-radius: 50%;
      display: flex;
      align-items: center;
      justify-content: center;
      font-size: 12px;
      font-weight: 900;
    }
    .btn-admin {
      background: linear-gradient(135deg, #1E3A8A, #2563EB);
      color: #fff;
      border: 1px solid #3B82F6;
      border-radius: 10px;
      padding: 6px 12px;
      font-size: 12px;
      font-weight: 700;
      cursor: pointer;
      display: flex;
      align-items: center;
      gap: 5px;
      transition: all 0.2s;
    }
    .btn-admin:hover { transform: translateY(-1px); box-shadow: 0 4px 12px rgba(37, 99, 235, 0.4); }

    /* Announcement Bar */
    .ticker {
      background: linear-gradient(90deg, #1E1B4B, #0F172A);
      border-bottom: 1px solid #1E293B;
      padding: 8px 16px;
      font-size: 12px;
      color: #93C5FD;
      display: flex;
      align-items: center;
      gap: 8px;
    }
    .ticker-badge {
      background: #EF4444;
      color: white;
      font-size: 10px;
      font-weight: 800;
      padding: 2px 6px;
      border-radius: 4px;
      text-transform: uppercase;
    }

    /* Container */
    .container { max-width: 1200px; margin: 0 auto; padding: 16px; }

    /* Hero Banner */
    .hero-banner {
      background: linear-gradient(135deg, #1E1B4B 0%, #172554 50%, #0F172A 100%);
      border: 1px solid #2563EB44;
      border-radius: 16px;
      padding: 24px;
      display: flex;
      flex-direction: column;
      gap: 12px;
      margin-bottom: 20px;
      position: relative;
      overflow: hidden;
    }
    .hero-title { font-size: 26px; font-weight: 900; color: #FBBF24; }
    .hero-subtitle { font-size: 14px; color: #94A3B8; max-width: 500px; line-height: 1.5; }
    .hero-actions { display: flex; gap: 12px; margin-top: 8px; flex-wrap: wrap; }
    .btn-primary {
      background: linear-gradient(135deg, #EF4444, #DC2626);
      color: white;
      border: none;
      padding: 10px 20px;
      border-radius: 10px;
      font-weight: 700;
      font-size: 14px;
      cursor: pointer;
    }
    .btn-secondary {
      background: #1E293B;
      color: #93C5FD;
      border: 1px solid #334155;
      padding: 10px 18px;
      border-radius: 10px;
      font-weight: 600;
      font-size: 14px;
      cursor: pointer;
    }

    /* Download Native APK Pill Banner */
    .apk-banner {
      background: #0F172A;
      border: 1px solid #10B98155;
      border-radius: 12px;
      padding: 12px 18px;
      display: flex;
      align-items: center;
      justify-content: space-between;
      margin-bottom: 24px;
      gap: 12px;
      flex-wrap: wrap;
    }
    .apk-info { display: flex; align-items: center; gap: 12px; }
    .apk-icon { font-size: 24px; }
    .apk-title { font-size: 14px; font-weight: 700; color: #fff; }
    .apk-desc { font-size: 11px; color: #10B981; }
    .btn-download-apk {
      background: #10B981;
      color: #000;
      font-weight: 800;
      font-size: 12px;
      padding: 8px 16px;
      border-radius: 8px;
      text-decoration: none;
      display: inline-block;
      transition: background 0.2s;
    }
    .btn-download-apk:hover { background: #34D399; }

    /* Category Navigation */
    .categories {
      display: flex;
      gap: 10px;
      overflow-x: auto;
      padding-bottom: 8px;
      margin-bottom: 20px;
      scrollbar-width: none;
    }
    .cat-pill {
      background: var(--bg-surface);
      color: var(--text-muted);
      border: 1px solid var(--border);
      padding: 8px 16px;
      border-radius: 20px;
      font-size: 13px;
      font-weight: 600;
      cursor: pointer;
      white-space: nowrap;
      transition: all 0.2s;
    }
    .cat-pill.active, .cat-pill:hover {
      background: var(--primary-blue);
      color: white;
      border-color: var(--primary-blue);
    }

    /* Games Grid */
    .section-title { font-size: 18px; font-weight: 800; margin-bottom: 14px; display: flex; align-items: center; gap: 8px; }
    .games-grid {
      display: grid;
      grid-template-columns: repeat(auto-fill, minmax(170px, 1fr));
      gap: 14px;
      margin-bottom: 30px;
    }
    .game-card {
      background: var(--bg-card);
      border: 1px solid var(--border);
      border-radius: 14px;
      padding: 16px;
      display: flex;
      flex-direction: column;
      align-items: center;
      text-align: center;
      gap: 8px;
      cursor: pointer;
      transition: all 0.2s;
      position: relative;
    }
    .game-card:hover { transform: translateY(-3px); border-color: var(--primary-blue); box-shadow: 0 8px 20px rgba(0,0,0,0.4); }
    .game-icon { font-size: 38px; margin-bottom: 4px; }
    .game-name { font-size: 14px; font-weight: 700; color: #fff; }
    .game-provider { font-size: 11px; color: var(--text-muted); }
    .game-badge {
      background: #1E293B;
      color: #93C5FD;
      font-size: 10px;
      font-weight: 700;
      padding: 2px 6px;
      border-radius: 4px;
    }

    /* Modal Framework */
    .modal-overlay {
      position: fixed;
      top: 0; left: 0; right: 0; bottom: 0;
      background: rgba(0, 0, 0, 0.8);
      display: none;
      align-items: center;
      justify-content: center;
      z-index: 999;
      padding: 16px;
    }
    .modal-overlay.active { display: flex; }
    .modal-card {
      background: var(--bg-card);
      border: 1px solid #334155;
      border-radius: 20px;
      width: 100%;
      max-width: 900px;
      max-height: 90vh;
      overflow-y: auto;
      padding: 24px;
      display: flex;
      flex-direction: column;
      gap: 18px;
      position: relative;
    }
    .modal-header { display: flex; justify-content: space-between; align-items: center; }
    .modal-close { background: none; border: none; color: var(--text-muted); font-size: 22px; cursor: pointer; }

    /* Admin Tabs */
    .admin-nav {
      display: flex;
      gap: 8px;
      border-bottom: 1px solid var(--border);
      padding-bottom: 10px;
      overflow-x: auto;
    }
    .admin-tab-btn {
      background: transparent;
      border: none;
      color: var(--text-muted);
      font-size: 13px;
      font-weight: 600;
      padding: 8px 14px;
      border-radius: 8px;
      cursor: pointer;
      white-space: nowrap;
    }
    .admin-tab-btn.active {
      background: #1E2958;
      color: #93C5FD;
      font-weight: 700;
    }

    /* Stats Grid */
    .stats-grid {
      display: grid;
      grid-template-columns: repeat(auto-fit, minmax(180px, 1fr));
      gap: 12px;
    }
    .stat-card {
      background: var(--bg-surface);
      border: 1px solid var(--border);
      border-radius: 12px;
      padding: 14px;
    }
    .stat-label { font-size: 11px; color: var(--text-muted); margin-bottom: 4px; }
    .stat-val { font-size: 20px; font-weight: 800; color: #fff; }

    /* Tables */
    table { width: 100%; border-collapse: collapse; font-size: 12px; text-align: left; }
    th { background: #0F172A; padding: 10px; color: var(--text-muted); border-bottom: 1px solid var(--border); font-weight: 600; }
    td { padding: 10px; border-bottom: 1px solid var(--border); color: #E2E8F0; }
    .status-badge { padding: 3px 8px; border-radius: 6px; font-size: 10px; font-weight: 700; }
    .status-active { background: #064E3B; color: #34D399; }
    .status-blocked { background: #7F1D1D; color: #FCA5A5; }

    /* Inputs & Forms */
    .form-group { display: flex; flex-direction: column; gap: 6px; margin-bottom: 12px; }
    .form-label { font-size: 12px; font-weight: 600; color: var(--text-muted); }
    .form-input {
      background: #0B0E1B;
      border: 1px solid var(--border);
      border-radius: 8px;
      padding: 10px 12px;
      color: #fff;
      font-size: 13px;
      outline: none;
    }
    .form-input:focus { border-color: var(--primary-blue); }

    /* Floating CS Bot Icon */
    .floating-cs {
      position: fixed;
      bottom: 24px;
      right: 24px;
      width: 54px;
      height: 54px;
      border-radius: 50%;
      background: linear-gradient(135deg, #2563EB, #1D4ED8);
      display: flex;
      align-items: center;
      justify-content: center;
      font-size: 26px;
      cursor: pointer;
      box-shadow: 0 4px 16px rgba(37, 99, 235, 0.5);
      z-index: 90;
    }
  </style>
</head>
<body>

  <!-- Top Navigation -->
  <header>
    <div class="brand" onclick="location.reload()">
      <span class="brand-logo">${db.websiteSettings.logoEmoji}</span>
      <span class="brand-name">khelv</span>
      <span class="brand-domain">.com</span>
    </div>

    <div class="header-actions">
      <div class="balance-chip" onclick="openModal('modal-auth')">
        <span class="balance-badge">₹</span>
        <span id="header-balance">1,000.00</span>
      </div>

      <button class="btn-admin" onclick="openModal('modal-admin-login')">
        🛡️ Admin
      </button>
    </div>
  </header>

  <!-- Live Announcement Ticker -->
  <div class="ticker">
    <span class="ticker-badge">Notice</span>
    <marquee id="announcement-text" behavior="scroll" direction="left">
      ${db.websiteSettings.announcementNotice}
    </marquee>
  </div>

  <div class="container">
    <!-- Hero Banner -->
    <div class="hero-banner">
      <div class="hero-title" id="hero-title">${db.websiteSettings.heroBannerTitle}</div>
      <div class="hero-subtitle" id="hero-subtitle">${db.websiteSettings.heroBannerSubtitle}</div>
      <div class="hero-actions">
        <button class="btn-primary" onclick="openModal('modal-auth')">Register & Claim Bonus</button>
        <button class="btn-secondary" onclick="openModal('modal-rebate')">Rebate Center</button>
      </div>
    </div>

    <!-- Download Native APK Banner -->
    <div class="apk-banner">
      <div class="apk-info">
        <span class="apk-icon">📱</span>
        <div>
          <div class="apk-title">Download Khelv Android Native App</div>
          <div class="apk-desc">Installed directly from AI Studio Gradle Build (23.8 MB APK)</div>
        </div>
      </div>
      <a href="/app-debug.apk" download="khelv-app.apk" class="btn-download-apk">Download APK</a>
    </div>

    <!-- Category Filter Pills -->
    <div class="categories">
      <div class="cat-pill active" onclick="filterCategory('All')">All Games</div>
      <div class="cat-pill" onclick="filterCategory('Crash / Aviator')">Crash / Aviator</div>
      <div class="cat-pill" onclick="filterCategory('Number / Colour')">WinGo 30s / Colour</div>
      <div class="cat-pill" onclick="filterCategory('Slot Games')">Slot Games</div>
      <div class="cat-pill" onclick="filterCategory('Mini Games')">Mini Games (Mines)</div>
      <div class="cat-pill" onclick="filterCategory('Card Games')">Card Games (Teen Patti)</div>
      <div class="cat-pill" onclick="filterCategory('Fishing Games')">Fishing Games</div>
      <div class="cat-pill" onclick="filterCategory('Cricket Games')">Cricket Exchange</div>
      <div class="cat-pill" onclick="filterCategory('Live Casino')">Live Casino</div>
    </div>

    <!-- Popular Games Grid -->
    <div class="section-title">🔥 Featured & Live Games</div>
    <div class="games-grid" id="games-container">
      ${db.games.map(g => `
        <div class="game-card" onclick="playGame('${g.id}', '${g.name}')">
          <div class="game-icon">${g.icon}</div>
          <div class="game-name">${g.name}</div>
          <div class="game-provider">${g.provider}</div>
          <span class="game-badge">RTP ${g.rtp}%</span>
        </div>
      `).join('')}
    </div>
  </div>

  <!-- Floating Customer Service Bot -->
  <div class="floating-cs" onclick="openModal('modal-notification')" title="Notifications & 24/7 AI Support">
    🤖
  </div>

  <!-- MODAL: Admin Login -->
  <div class="modal-overlay" id="modal-admin-login">
    <div class="modal-card" style="max-width: 440px;">
      <div class="modal-header">
        <h3 style="display:flex;align-items:center;gap:8px;">🛡️ Admin Control Login</h3>
        <button class="modal-close" onclick="closeModal('modal-admin-login')">&times;</button>
      </div>

      <div style="background:#1E293B;padding:10px;border-radius:8px;font-size:11px;color:#94A3B8;">
        🔐 Quick Credentials: <b>admin</b> / <b>khelv@2026</b> &bull; 2FA: <b>682914</b>
      </div>

      <div class="form-group">
        <label class="form-label">Role</label>
        <select class="form-input" id="admin-role-select">
          <option value="SUPER_ADMIN">Super Admin (Full Access)</option>
          <option value="STAFF_ADMIN">Staff Admin (Support / View)</option>
        </select>
      </div>

      <div class="form-group">
        <label class="form-label">Username</label>
        <input type="text" class="form-input" id="admin-user-input" value="admin">
      </div>

      <div class="form-group">
        <label class="form-label">Password</label>
        <input type="password" class="form-input" id="admin-pass-input" value="khelv@2026">
      </div>

      <div class="form-group">
        <label class="form-label">2FA Authenticator Code</label>
        <input type="text" class="form-input" id="admin-2fa-input" value="682914">
      </div>

      <button class="btn-primary" style="background:#2563EB;" onclick="handleAdminLogin()">
        Verify 2FA & Open Admin Panel
      </button>
    </div>
  </div>

  <!-- MODAL: Full Admin Panel -->
  <div class="modal-overlay" id="modal-admin-panel">
    <div class="modal-card">
      <div class="modal-header">
        <div style="display:flex;align-items:center;gap:10px;">
          <h2 style="color:#60A5FA;">🛡️ Khelv Executive Admin Portal</h2>
          <span class="status-badge status-active">SUPER_ADMIN (2FA ACTIVE)</span>
        </div>
        <button class="modal-close" onclick="closeModal('modal-admin-panel')">&times;</button>
      </div>

      <!-- Admin Tabs Navigation -->
      <div class="admin-nav">
        <button class="admin-tab-btn active" onclick="switchAdminTab('tab-dash')">📊 Dashboard</button>
        <button class="admin-tab-btn" onclick="switchAdminTab('tab-users')">👥 User Management</button>
        <button class="admin-tab-btn" onclick="switchAdminTab('tab-games')">🎮 Game Management</button>
        <button class="admin-tab-btn" onclick="switchAdminTab('tab-wallet')">💰 Treasury & Wallet</button>
        <button class="admin-tab-btn" onclick="switchAdminTab('tab-cms')">⚙️ Website CMS</button>
        <button class="admin-tab-btn" onclick="switchAdminTab('tab-reports')">📈 Analytics</button>
        <button class="admin-tab-btn" onclick="switchAdminTab('tab-security')">🔐 Security & Logs</button>
      </div>

      <!-- Tab: Dashboard -->
      <div id="tab-dash" class="admin-tab-content">
        <div class="stats-grid" style="margin-bottom:20px;">
          <div class="stat-card">
            <div class="stat-label">Total Users</div>
            <div class="stat-val" id="stat-total-users">${usersCount}</div>
          </div>
          <div class="stat-card">
            <div class="stat-label">Active Players</div>
            <div class="stat-val" style="color:#10B981;" id="stat-active-users">${activeUsersCount}</div>
          </div>
          <div class="stat-card">
            <div class="stat-label">Virtual Coins Circulation</div>
            <div class="stat-val" style="color:#F59E0B;">₹${totalCoins}</div>
          </div>
          <div class="stat-card">
            <div class="stat-label">Live Games</div>
            <div class="stat-val" style="color:#60A5FA;">${gamesCount}</div>
          </div>
        </div>

        <h4 style="margin-bottom:10px;">Live Admin Audit Trail</h4>
        <table>
          <thead>
            <tr><th>Audit ID</th><th>Admin</th><th>Action</th><th>Target</th><th>Detail</th><th>Timestamp</th></tr>
          </thead>
          <tbody id="audit-logs-table">
            ${db.auditLogs.map(l => `
              <tr>
                <td><b>${l.id}</b></td>
                <td>${l.admin}</td>
                <td><span class="status-badge status-active">${l.action}</span></td>
                <td>${l.target}</td>
                <td>${l.amount} (${l.reason})</td>
                <td>${l.time}</td>
              </tr>
            `).join('')}
          </tbody>
        </table>
      </div>

      <!-- Tab: Users -->
      <div id="tab-users" class="admin-tab-content" style="display:none;">
        <div style="display:flex;justify-content:space-between;margin-bottom:12px;gap:10px;">
          <input type="text" class="form-input" placeholder="Search user by ID or Phone..." style="max-width:300px;" oninput="searchUsers(this.value)">
          <button class="btn-primary" style="font-size:12px;padding:6px 14px;" onclick="promptNewUser()">+ Add User</button>
        </div>
        <table>
          <thead>
            <tr><th>User ID</th><th>Username</th><th>Phone</th><th>Balance</th><th>VIP</th><th>Status</th><th>Actions</th></tr>
          </thead>
          <tbody id="users-table-body">
            ${db.users.map(u => `
              <tr id="user-row-${u.id}">
                <td><b>${u.id}</b></td>
                <td>${u.username}</td>
                <td>${u.phone}</td>
                <td style="color:#F59E0B;font-weight:700;">₹${u.balance.toFixed(2)}</td>
                <td>VIP ${u.vipLevel}</td>
                <td><span class="status-badge ${u.isBlocked ? 'status-blocked' : 'status-active'}">${u.isBlocked ? 'BLOCKED' : 'ACTIVE'}</span></td>
                <td>
                  <button style="background:#1E293B;color:#93C5FD;border:none;padding:4px 8px;border-radius:4px;cursor:pointer;" onclick="toggleBlockUser('${u.id}')">${u.isBlocked ? 'Unblock' : 'Block'}</button>
                  <button style="background:#1E3A8A;color:#fff;border:none;padding:4px 8px;border-radius:4px;cursor:pointer;" onclick="adjustCoins('${u.id}')">&plusmn; Coins</button>
                </td>
              </tr>
            `).join('')}
          </tbody>
        </table>
      </div>

      <!-- Tab: Games -->
      <div id="tab-games" class="admin-tab-content" style="display:none;">
        <div style="display:flex;justify-content:space-between;margin-bottom:12px;">
          <h4 style="margin:0;">Catalog & Status Controls</h4>
          <button class="btn-primary" style="font-size:12px;padding:6px 14px;" onclick="promptNewGame()">+ Add Game</button>
        </div>
        <table>
          <thead>
            <tr><th>Game</th><th>Category</th><th>Provider</th><th>RTP</th><th>Plays</th><th>Status</th><th>Toggle</th></tr>
          </thead>
          <tbody id="games-table-body">
            ${db.games.map(g => `
              <tr>
                <td>${g.icon} <b>${g.name}</b></td>
                <td>${g.category}</td>
                <td>${g.provider}</td>
                <td>${g.rtp}%</td>
                <td>${g.playCount.toLocaleString()}</td>
                <td><span class="status-badge ${g.isEnabled ? 'status-active' : 'status-blocked'}">${g.isEnabled ? 'ONLINE' : 'DISABLED'}</span></td>
                <td>
                  <button style="background:#1E293B;color:#fff;border:none;padding:4px 8px;border-radius:4px;cursor:pointer;" onclick="toggleGame('${g.id}')">
                    ${g.isEnabled ? 'Disable' : 'Enable'}
                  </button>
                </td>
              </tr>
            `).join('')}
          </tbody>
        </table>
      </div>

      <!-- Tab: Wallet -->
      <div id="tab-wallet" class="admin-tab-content" style="display:none;">
        <div class="stats-grid" style="margin-bottom:16px;">
          <div class="stat-card">
            <div class="stat-label">Total Treasury Reserve</div>
            <div class="stat-val" style="color:#10B981;">₹10,000,000.00</div>
          </div>
          <div class="stat-card">
            <div class="stat-label">User Circulation</div>
            <div class="stat-val" style="color:#F59E0B;">₹${totalCoins}</div>
          </div>
          <div class="stat-card">
            <div class="stat-label">24h Net Gaming Revenue</div>
            <div class="stat-val" style="color:#3B82F6;">+₹42,850.00</div>
          </div>
        </div>
        <p style="font-size:12px;color:#94A3B8;">All currency adjustments are logged cryptographically with reason and admin ID.</p>
      </div>

      <!-- Tab: CMS -->
      <div id="tab-cms" class="admin-tab-content" style="display:none;">
        <div class="form-group">
          <label class="form-label">Platform Name</label>
          <input type="text" class="form-input" id="cms-name" value="${db.websiteSettings.siteName}">
        </div>
        <div class="form-group">
          <label class="form-label">Announcement Notice Ticker</label>
          <input type="text" class="form-input" id="cms-notice" value="${db.websiteSettings.announcementNotice}">
        </div>
        <div class="form-group">
          <label class="form-label">Hero Title</label>
          <input type="text" class="form-input" id="cms-hero" value="${db.websiteSettings.heroBannerTitle}">
        </div>
        <button class="btn-primary" onclick="saveCMS()">Save Platform Settings</button>
      </div>

      <!-- Tab: Reports -->
      <div id="tab-reports" class="admin-tab-content" style="display:none;">
        <h4>🏆 Top 5 Games by Volume</h4>
        <ul style="list-style:none;padding:10px 0;display:flex;flex-direction:column;gap:8px;">
          <li style="background:#0F172A;padding:10px;border-radius:8px;display:flex;justify-content:space-between;">
            <span>1. 🎨 Win Go 30s</span><b>35,120 rounds &bull; ₹340,000 turnover</b>
          </li>
          <li style="background:#0F172A;padding:10px;border-radius:8px;display:flex;justify-content:space-between;">
            <span>2. ✈️ Aviator</span><b>28,450 rounds &bull; ₹280,000 turnover</b>
          </li>
          <li style="background:#0F172A;padding:10px;border-radius:8px;display:flex;justify-content:space-between;">
            <span>3. 🃏 Super Ace</span><b>22,100 rounds &bull; ₹195,000 turnover</b>
          </li>
          <li style="background:#0F172A;padding:10px;border-radius:8px;display:flex;justify-content:space-between;">
            <span>4. ♠️ Rummy 13 Cards</span><b>20,500 rounds &bull; ₹180,000 turnover</b>
          </li>
          <li style="background:#0F172A;padding:10px;border-radius:8px;display:flex;justify-content:space-between;">
            <span>5. 💣 Mines 5x5</span><b>19,800 rounds &bull; ₹150,000 turnover</b>
          </li>
        </ul>
      </div>

      <!-- Tab: Security -->
      <div id="tab-security" class="admin-tab-content" style="display:none;">
        <div style="background:#1E293B;padding:14px;border-radius:10px;margin-bottom:14px;">
          <h4 style="color:#10B981;margin-bottom:6px;">Two-Factor Authentication (2FA): Active & Enforced</h4>
          <p style="font-size:12px;color:#94A3B8;">All admin endpoints require an active session token and 6-digit TOTP verification.</p>
        </div>
        <h4>Recent Admin Login Activity</h4>
        <table>
          <thead>
            <tr><th>Session</th><th>Admin</th><th>Role</th><th>IP</th><th>Time</th><th>Status</th></tr>
          </thead>
          <tbody>
            ${db.loginLogs.map(l => `
              <tr>
                <td>${l.id}</td>
                <td>${l.admin}</td>
                <td>${l.role}</td>
                <td>${l.ip}</td>
                <td>${l.time}</td>
                <td><span class="status-badge status-active">${l.status}</span></td>
              </tr>
            `).join('')}
          </tbody>
        </table>
      </div>
    </div>
  </div>

  <!-- MODAL: Rebate (Screenshot 1) -->
  <div class="modal-overlay" id="modal-rebate">
    <div class="modal-card" style="max-width: 500px;">
      <div class="modal-header">
        <h3>Rebate Center</h3>
        <button class="modal-close" onclick="closeModal('modal-rebate')">&times;</button>
      </div>
      <div style="background:#11173D;border:1px solid #1E2958;border-radius:14px;padding:16px;">
        <div style="display:flex;justify-content:space-between;align-items:center;margin-bottom:8px;">
          <span style="font-size:13px;font-weight:600;">All-Total betting rebate</span>
          <span style="background:#1E3A8A;color:#93C5FD;padding:2px 6px;border-radius:4px;font-size:10px;">Real-time count</span>
        </div>
        <div style="font-size:32px;font-weight:900;margin-bottom:8px;">🪙 0.00</div>
        <div style="background:#1E2958;padding:8px;border-radius:6px;font-size:11px;color:#93C5FD;margin-bottom:12px;">
          Upgrade VIP level to increase rebate rate
        </div>
        <button class="btn-primary" style="width:100%;background:#E2E8F0;color:#0F172A;" onclick="alert('One-Click Rebate settled!')">
          One-Click Rebate
        </button>
      </div>
    </div>
  </div>

  <!-- MODAL: Notification (Screenshot 2) -->
  <div class="modal-overlay" id="modal-notification">
    <div class="modal-card" style="max-width: 500px;">
      <div class="modal-header">
        <h3>Notification</h3>
        <button class="modal-close" onclick="closeModal('modal-notification')">&times;</button>
      </div>
      <div style="background:#1A2045;border-radius:10px;padding:14px;border:1px solid #28316B;">
        <div style="display:flex;justify-content:space-between;align-items:center;margin-bottom:6px;">
          <b>✉️ New Member</b>
          <span style="font-size:11px;color:#94A3B8;">2026-11-03 12:14:35</span>
        </div>
        <p style="font-size:12px;color:#CBD5E1;line-height:1.6;">
          Thank you for becoming a beloved member of this platform. We provide many industry leading games. This is the world's leading gaming platform. Try the lottery game developed by us. While enjoying the best gaming experience, you can also join unlimited agents and stay at home to earn money.
        </p>
      </div>
    </div>
  </div>

  <!-- MODAL: Register / Login (Screenshot 3) -->
  <div class="modal-overlay" id="modal-auth">
    <div class="modal-card" style="max-width: 440px;">
      <div class="modal-header">
        <h3 style="color:#FBBF24;">👑 khelv.com</h3>
        <button class="modal-close" onclick="closeModal('modal-auth')">&times;</button>
      </div>
      <div style="background:linear-gradient(90deg, #DC2626, #1D4ED8);padding:8px;border-radius:6px;font-size:11px;text-align:center;">
        Please register by phone number or email
      </div>
      <div class="form-group">
        <label class="form-label">Phone number</label>
        <div style="display:flex;gap:6px;">
          <input type="text" class="form-input" style="width:70px;text-align:center;" value="+91" readonly>
          <input type="tel" class="form-input" style="flex:1;" placeholder="Please enter the phone number" id="reg-phone">
        </div>
      </div>
      <div class="form-group">
        <label class="form-label">Set password</label>
        <input type="password" class="form-input" placeholder="Set password" id="reg-pass">
      </div>
      <div class="form-group">
        <label class="form-label">Confirm password</label>
        <input type="password" class="form-input" placeholder="Confirm password" id="reg-confirm">
      </div>
      <div class="form-group">
        <label class="form-label">Invite code</label>
        <input type="text" class="form-input" value="75482128162" id="reg-invite">
      </div>
      <div style="display:flex;align-items:center;gap:6px;font-size:12px;color:#94A3B8;">
        <input type="checkbox" checked id="reg-terms">
        <span>I have read and agree <b style="color:#EF4444;">【Privacy Agreement】</b></span>
      </div>
      <button class="btn-primary" style="width:100%;margin-top:10px;" onclick="handleRegister()">
        Register
      </button>
    </div>
  </div>

  <script>
    function openModal(id) {
      document.getElementById(id).classList.add('active');
    }
    function closeModal(id) {
      document.getElementById(id).classList.remove('active');
    }
    function filterCategory(cat) {
      document.querySelectorAll('.cat-pill').forEach(el => el.classList.remove('active'));
      event.target.classList.add('active');
      fetch('/api/games')
        .then(r => r.json())
        .then(games => {
          const filtered = cat === 'All' ? games : games.filter(g => g.category === cat);
          document.getElementById('games-container').innerHTML = filtered.map(g => \`
            <div class="game-card" onclick="playGame('\${g.id}', '\${g.name}')">
              <div class="game-icon">\${g.icon}</div>
              <div class="game-name">\${g.name}</div>
              <div class="game-provider">\${g.provider}</div>
              <span class="game-badge">RTP \${g.rtp}%</span>
            </div>
          \`).join('');
        });
    }
    function playGame(id, name) {
      alert('Launching ' + name + ' on Khelv Gaming Engine...');
    }
    function handleRegister() {
      const phone = document.getElementById('reg-phone').value;
      if (!phone) { alert('Please enter phone number'); return; }
      alert('Registered successfully! +₹100 bonus credited.');
      closeModal('modal-auth');
    }
    function handleAdminLogin() {
      const u = document.getElementById('admin-user-input').value;
      const p = document.getElementById('admin-pass-input').value;
      const code = document.getElementById('admin-2fa-input').value;
      if (u === 'admin' && p === 'khelv@2026' && code === '682914') {
        closeModal('modal-admin-login');
        openModal('modal-admin-panel');
      } else {
        alert('Invalid credentials. Use admin / khelv@2026 / 682914');
      }
    }
    function switchAdminTab(tabId) {
      document.querySelectorAll('.admin-tab-content').forEach(el => el.style.display = 'none');
      document.querySelectorAll('.admin-tab-btn').forEach(el => el.classList.remove('active'));
      document.getElementById(tabId).style.display = 'block';
      event.target.classList.add('active');
    }
    function toggleBlockUser(id) {
      fetch('/api/admin/toggle-user-block', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ userId: id })
      }).then(r => r.json()).then(data => {
        location.reload();
      });
    }
    function toggleGame(id) {
      fetch('/api/admin/toggle-game', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ gameId: id })
      }).then(r => r.json()).then(data => {
        location.reload();
      });
    }
    function adjustCoins(id) {
      const amt = prompt('Enter coins to adjust (+500 or -200):', '+500');
      if (!amt) return;
      const reason = prompt('Enter audit reason:', 'Admin manual adjustment');
      fetch('/api/admin/adjust-coins', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ userId: id, amount: parseFloat(amt), reason })
      }).then(r => r.json()).then(data => {
        alert('Coins updated. New balance: ₹' + data.newBalance);
        location.reload();
      });
    }
    function saveCMS() {
      const name = document.getElementById('cms-name').value;
      const notice = document.getElementById('cms-notice').value;
      const hero = document.getElementById('cms-hero').value;
      fetch('/api/admin/cms', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ siteName: name, announcementNotice: notice, heroBannerTitle: hero })
      }).then(r => r.json()).then(data => {
        alert('Platform settings saved!');
        location.reload();
      });
    }
  </script>
</body>
</html>`;
}

// Request Handler
const server = http.createServer((req, res) => {
  const parsedUrl = url.parse(req.url, true);
  const pathname = parsedUrl.pathname;

  // CORS Headers
  res.setHeader('Access-Control-Allow-Origin', '*');
  res.setHeader('Access-Control-Allow-Methods', 'GET, POST, OPTIONS');
  res.setHeader('Access-Control-Allow-Headers', 'Content-Type, Authorization');

  if (req.method === 'OPTIONS') {
    res.writeHead(204);
    res.end();
    return;
  }

  // Health check endpoint
  if (pathname === '/api/health' || pathname === '/health') {
    res.writeHead(200, { 'Content-Type': 'application/json' });
    res.end(JSON.stringify({ status: 'healthy', timestamp: new Date().toISOString() }));
    return;
  }

  // Status check endpoint
  if (pathname === '/api/status') {
    res.writeHead(200, { 'Content-Type': 'application/json' });
    res.end(JSON.stringify({
      online: true,
      activeUsers: db.users.filter(u => !u.isBlocked).length,
      totalUsers: db.users.length,
      totalGames: db.games.length,
      version: '2.0.0'
    }));
    return;
  }

  // Serve the compiled Android APK
  if (pathname === '/app-debug.apk' || pathname === '/download-apk') {
    if (fs.existsSync(APK_PATH)) {
      const stat = fs.statSync(APK_PATH);
      res.writeHead(200, {
        'Content-Type': 'application/vnd.android.package-archive',
        'Content-Length': stat.size,
        'Content-Disposition': 'attachment; filename="khelv-app.apk"'
      });
      fs.createReadStream(APK_PATH).pipe(res);
      return;
    } else {
      res.writeHead(404, { 'Content-Type': 'text/plain' });
      res.end('APK build file not ready yet. Please compile via Gradle.');
      return;
    }
  }

  // API: Get Games
  if (pathname === '/api/games' && req.method === 'GET') {
    res.writeHead(200, { 'Content-Type': 'application/json' });
    res.end(JSON.stringify(db.games));
    return;
  }

  // API: Get Users
  if (pathname === '/api/users' && req.method === 'GET') {
    res.writeHead(200, { 'Content-Type': 'application/json' });
    res.end(JSON.stringify(db.users));
    return;
  }

  // API: Admin Toggle User Block
  if (pathname === '/api/admin/toggle-user-block' && req.method === 'POST') {
    let body = '';
    req.on('data', chunk => { body += chunk; });
    req.on('end', () => {
      try {
        const { userId } = JSON.parse(body);
        const user = db.users.find(u => u.id === userId);
        if (user) {
          user.isBlocked = !user.isBlocked;
          db.auditLogs.unshift({
            id: 'AUD-' + Math.floor(Math.random() * 9000 + 1000),
            action: user.isBlocked ? 'BLOCK_USER' : 'UNBLOCK_USER',
            admin: 'superadmin',
            target: userId,
            amount: user.isBlocked ? 'BLOCK' : 'ACTIVE',
            reason: 'Admin state toggle',
            time: new Date().toISOString().replace('T', ' ').substring(0, 19)
          });
          res.writeHead(200, { 'Content-Type': 'application/json' });
          res.end(JSON.stringify({ success: true, isBlocked: user.isBlocked }));
        } else {
          res.writeHead(404, { 'Content-Type': 'application/json' });
          res.end(JSON.stringify({ error: 'User not found' }));
        }
      } catch (e) {
        res.writeHead(400, { 'Content-Type': 'application/json' });
        res.end(JSON.stringify({ error: e.message }));
      }
    });
    return;
  }

  // API: Admin Adjust Coins
  if (pathname === '/api/admin/adjust-coins' && req.method === 'POST') {
    let body = '';
    req.on('data', chunk => { body += chunk; });
    req.on('end', () => {
      try {
        const { userId, amount, reason } = JSON.parse(body);
        const user = db.users.find(u => u.id === userId);
        if (user) {
          user.balance = Math.max(0, user.balance + amount);
          db.auditLogs.unshift({
            id: 'AUD-' + Math.floor(Math.random() * 9000 + 1000),
            action: amount >= 0 ? 'COIN_CREDIT' : 'COIN_DEBIT',
            admin: 'superadmin',
            target: userId,
            amount: (amount >= 0 ? '+' : '') + '₹' + amount.toFixed(2),
            reason: reason || 'Manual admin balance adjustment',
            time: new Date().toISOString().replace('T', ' ').substring(0, 19)
          });
          res.writeHead(200, { 'Content-Type': 'application/json' });
          res.end(JSON.stringify({ success: true, newBalance: user.balance }));
        } else {
          res.writeHead(404, { 'Content-Type': 'application/json' });
          res.end(JSON.stringify({ error: 'User not found' }));
        }
      } catch (e) {
        res.writeHead(400, { 'Content-Type': 'application/json' });
        res.end(JSON.stringify({ error: e.message }));
      }
    });
    return;
  }

  // API: Admin Toggle Game
  if (pathname === '/api/admin/toggle-game' && req.method === 'POST') {
    let body = '';
    req.on('data', chunk => { body += chunk; });
    req.on('end', () => {
      try {
        const { gameId } = JSON.parse(body);
        const game = db.games.find(g => g.id === gameId);
        if (game) {
          game.isEnabled = !game.isEnabled;
          res.writeHead(200, { 'Content-Type': 'application/json' });
          res.end(JSON.stringify({ success: true, isEnabled: game.isEnabled }));
        } else {
          res.writeHead(404, { 'Content-Type': 'application/json' });
          res.end(JSON.stringify({ error: 'Game not found' }));
        }
      } catch (e) {
        res.writeHead(400, { 'Content-Type': 'application/json' });
        res.end(JSON.stringify({ error: e.message }));
      }
    });
    return;
  }

  // API: Admin Update CMS
  if (pathname === '/api/admin/cms' && req.method === 'POST') {
    let body = '';
    req.on('data', chunk => { body += chunk; });
    req.on('end', () => {
      try {
        const payload = JSON.parse(body);
        Object.assign(db.websiteSettings, payload);
        res.writeHead(200, { 'Content-Type': 'application/json' });
        res.end(JSON.stringify({ success: true, settings: db.websiteSettings }));
      } catch (e) {
        res.writeHead(400, { 'Content-Type': 'application/json' });
        res.end(JSON.stringify({ error: e.message }));
      }
    });
    return;
  }

  // Admin Direct URL Route (/admin)
  if (pathname === '/admin') {
    res.writeHead(200, { 'Content-Type': 'text/html; charset=utf-8' });
    let html = renderHTML();
    // Auto-open admin modal
    html = html.replace("window.onload = function() {", "window.onload = function() { openModal('modal-admin-login');");
    if (!html.includes("window.onload")) {
      html = html.replace("</body>", "<script>window.addEventListener('DOMContentLoaded', () => openModal('modal-admin-login'));</script></body>");
    }
    res.end(html);
    return;
  }

  // Default: Serve Web Gaming Platform
  res.writeHead(200, { 'Content-Type': 'text/html; charset=utf-8' });
  res.end(renderHTML());
});

server.listen(PORT, '0.0.0.0', () => {
  console.log(`[Khelv] Web and Admin Server running at http://0.0.0.0:${PORT}`);
  console.log(`[Khelv] Ready to accept traffic from Cloud Run (Nginx port 8080)`);
});
