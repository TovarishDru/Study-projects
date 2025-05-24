import sqlite3
from sqlite3 import Error
import hashlib
import secrets

class Database:
    def __init__(self, db_name="users.db"):
        self.db_name = db_name
        self.connection = sqlite3.connect(self.db_name)
        self.cursor = self.connection.cursor()

    def _initialize_db(self):
        try:            
            self.cursor.execute('''
                CREATE TABLE IF NOT EXISTS users (
                    username TEXT UNIQUE NOT NULL,
                    password TEXT NOT NULL,
                    salt TEXT NOT NULL,
                    wins INTEGER DEFAULT 0,
                    losses INTEGER DEFAULT 0,
                    total_matches INTEGER DEFAULT 0
                )
            ''')
            self.connection.commit()
        except Error as e:
            print(f"Error when connecting to database: {e}")
            
    def _hash_password(self, password: str, salt: str = None) -> tuple:
        """
            Generates a salt and password hash using PBKDF2-HMAC-SHA256.
            If the salt is not passed, it creates a new one.
        """
        if salt is None:
            salt = secrets.token_hex(16)

        password_hash = hashlib.pbkdf2_hmac(
            'sha256',
            password.encode('utf-8'),
            salt.encode('utf-8'),
            100000
        ).hex()
        return password_hash, salt

    def add_user(self, username: str, password: str) -> bool:
        try:
            password_hash, salt = self._hash_password(password)
            self.cursor.execute('''
                INSERT INTO users (username, password, salt)
                VALUES (?, ?, ?)
            ''', (username, password_hash, salt))
            self.connection.commit()
            return True
        except Error as e:
            print(f"Error when adding a user: {e}")
            return False

    def verify_user(self, username: str, password: str) -> bool:
        user_data = self.get_user(username)
        if not user_data:
            return False
        
        stored_hash = user_data[1]
        salt = user_data[2]
        
        new_hash, _ = self._hash_password(password, salt)
        return secrets.compare_digest(new_hash, stored_hash)

    def get_user(self, username: str):
        try:
            self.cursor.execute('''
                SELECT * FROM users WHERE username = ?
            ''', (username,))
            return self.cursor.fetchone()
        except Error as e:
            print(f"Error when getting user: {e}")
            return None

    def update_stats(self, username: str, win: bool = False) -> bool:
        try:
            if win:
                self.cursor.execute('''
                    UPDATE users 
                    SET wins = wins + 1, 
                        total_matches = total_matches + 1 
                    WHERE username = ?
                ''', (username,))
            else:
                self.cursor.execute('''
                    UPDATE users 
                    SET losses = losses + 1, 
                        total_matches = total_matches + 1 
                    WHERE username = ?
                ''', (username,))
            self.connection.commit()
            return True
        except Error as e:
            print(f"Error when updating statistic: {e}")
            return False

    def delete_user(self, username: str) -> bool:
        try:
            self.cursor.execute('''
                DELETE FROM users WHERE username = ?
            ''', (username,))
            self.connection.commit()
            return True
        except Error as e:
            print(f"Error when user deleting: {e}")
            return False

    def close(self):
        if self.connection:
            self.connection.close()