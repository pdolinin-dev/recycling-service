import React, { useState, useEffect } from 'react';
import { TextField, IconButton, List, ListItem, ListItemText, ListItemAvatar, Avatar, Typography } from '@material-ui/core';
import Stomp from 'stompjs';
import SockJS from 'sockjs-client';
import './App.css'; // Изменил на обычный CSS-импорт

const App = () => {
  const [messages, setMessages] = useState([]);
  const [message, setMessage] = useState('');
  const [nickname, setNickname] = useState('');
  const [stompClient, setStompClient] = useState(null);

  useEffect(() => {
    const socket = new SockJS('http://localhost:8080/ws');
    const client = Stomp.over(socket);

    client.connect({}, () => {
      client.subscribe('/topic/messages', (message) => {
        const receivedMessage = JSON.parse(message.body);
        setMessages((prevMessages) => [...prevMessages, receivedMessage]);
      });
    });

    setStompClient(client);

    return () => {
      client.disconnect();
    };
  }, []);

  const handleNicknameChange = (event) => {
    setNickname(event.target.value);
  };

  const handleMessageChange = (event) => {
    setMessage(event.target.value);
  };

  const sendMessage = () => {
    if (message.trim()) {
      const chatMessage = {
        nickname,
        content: message,
      };

      stompClient.send('/app/chat', {}, JSON.stringify(chatMessage));
      setMessage('');
    }
  };

  return (
    <div className="container">
      <List className="chat-box">
        {messages.map((msg, index) => (
          <ListItem key={index} className="message">
            <ListItemAvatar>
              <Avatar className="avatar">{msg.nickname.charAt(0)}</Avatar>
            </ListItemAvatar>
            <ListItemText
              primary={<Typography variant="subtitle1">{msg.nickname}</Typography>}
              secondary={msg.content}
            />
          </ListItem>
        ))}
      </List>

      <div className="send-message">
        <TextField
          placeholder="Enter your nickname"
          value={nickname}
          onChange={handleNicknameChange}
          autoFocus
          className="input-field"
        />
        <TextField
          placeholder="Type a message"
          value={message}
          onChange={handleMessageChange}
          fullWidth
          className="input-message"
        />
        <IconButton onClick={sendMessage} disabled={!message.trim()} className="send-button">
          send
        </IconButton>
        <div className="chat-buttons">
          <button className="btn-attach">Прикрепить файл</button>
          <button className="btn-emoji">Эмодзи</button>
          <button className="btn-clear">Очистить чат</button>
        </div>
      </div>
    </div>
  );
};

export default App;