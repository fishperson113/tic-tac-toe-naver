import { WebSocketServer } from 'ws';

const PORT = process.env.PORT || 8080;
const wss = new WebSocketServer({ port: PORT, path: '/ws' });

let connections = [];
let roles = new Map(); // ws -> 'ODD' | 'EVEN'
let board = Array(25).fill(0);
let gameOver = false;

const lines = [
  // rows
  [0,1,2,3,4],[5,6,7,8,9],[10,11,12,13,14],[15,16,17,18,19],[20,21,22,23,24],
  // cols
  [0,5,10,15,20],[1,6,11,16,21],[2,7,12,17,22],[3,8,13,18,23],[4,9,14,19,24],
  // diagonals
  [0,6,12,18,24],[4,8,12,16,20]
];

function isOddWin(values) {
  return values.every(v => v % 2 === 1);
}
function isEvenWin(values) {
  return values.every(v => v % 2 === 0 && v > 0);
}

function broadcast(obj) {
  const msg = JSON.stringify(obj);
  connections.forEach(ws => {
    if (ws.readyState === ws.OPEN) ws.send(msg);
  });
}

function checkWinAndBroadcast() {
  for (const line of lines) {
    const values = line.map(i => board[i]);
    if (isOddWin(values)) {
      gameOver = true;
      broadcast({ type: 'GAME_OVER', winner: 'ODD', winningLine: line });
      return;
    }
    if (isEvenWin(values)) {
      gameOver = true;
      broadcast({ type: 'GAME_OVER', winner: 'EVEN', winningLine: line });
      return;
    }
  }
}

function assignRole(ws) {
  if (connections.length === 0) return 'ODD';
  if (connections.length === 1) return 'EVEN';
  return null;
}

function sendPlayerAssigned(ws, role) {
  ws.send(JSON.stringify({ type: 'PLAYER_ASSIGNED', player: role, board }));
}

wss.on('connection', (ws) => {
  if (connections.length >= 2) {
    ws.close(1000, 'Room full');
    return;
  }
  connections.push(ws);
  const role = assignRole(ws);
  roles.set(ws, role);
  sendPlayerAssigned(ws, role);

  if (connections.length === 2) {
    broadcast({ type: 'READY' });
  }

  ws.on('message', (data) => {
    try {
      const msg = JSON.parse(data.toString());
      if (gameOver) return; // ignore after game over
      if (connections.length < 2) return; // wait for opponent
      if (msg.type === 'INCREMENT') {
        const idx = Number(msg.square);
        if (Number.isInteger(idx) && idx >= 0 && idx < 25) {
          board[idx] = (board[idx] || 0) + 1;
          broadcast({ type: 'UPDATE', square: idx, value: board[idx] });
          checkWinAndBroadcast();
        }
      }
    } catch (e) {
      // ignore invalid
    }
  });

  ws.on('close', () => {
    connections = connections.filter(c => c !== ws);
    roles.delete(ws);
    if (!gameOver) {
      // end game immediately on disconnect
      gameOver = true;
      broadcast({ type: 'GAME_OVER', winner: null, winningLine: [] });
    }
  });
});

console.log(`WebSocket server listening on ws://localhost:${PORT}/ws`);

