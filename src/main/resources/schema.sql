CREATE TABLE IF NOT EXISTS users
(
    user_id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    name    VARCHAR(255)        NOT NULL,
    email   VARCHAR(320) UNIQUE NOT NULL
);

COMMENT ON TABLE users IS 'Пользователи';
COMMENT ON COLUMN users.user_id IS 'Идентификатор пользователя';
COMMENT ON COLUMN users.name IS 'Имя';
COMMENT ON COLUMN users.email IS 'Электронная почта';

CREATE TABLE IF NOT EXISTS items
(
    item_id     BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    owner_id    BIGINT REFERENCES users (user_id) ON DELETE CASCADE,
    name        VARCHAR(255)  NOT NULL,
    description VARCHAR(1000) NOT NULL,
    available   BOOLEAN       NOT NULL
);

COMMENT ON TABLE items IS 'Вещи';
COMMENT ON COLUMN items.item_id IS 'Идентификатор вещи';
COMMENT ON COLUMN items.owner_id IS 'Идентификатор пользователя';
COMMENT ON COLUMN items.name IS 'Название';
COMMENT ON COLUMN items.description IS 'Описание';
COMMENT ON COLUMN items.available IS 'Признак возможности бронирования';

CREATE TABLE IF NOT EXISTS bookings
(
    booking_id   BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    item_id      BIGINT REFERENCES items (item_id) ON DELETE CASCADE,
    initiator_id BIGINT REFERENCES users (user_id) ON DELETE CASCADE,
    start_dttm   TIMESTAMP   NOT NULL,
    end_dttm     TIMESTAMP   NOT NULL,
    status       VARCHAR(30) NOT NULL
);

COMMENT ON TABLE bookings IS 'Бронирования';
COMMENT ON COLUMN bookings.booking_id IS 'Идентификатор бронирования';
COMMENT ON COLUMN bookings.item_id IS 'Идентификатор вещи';
COMMENT ON COLUMN bookings.initiator_id IS 'Идентификатор создателя бронирования';
COMMENT ON COLUMN bookings.start_dttm IS 'Начало действия';
COMMENT ON COLUMN bookings.end_dttm IS 'Окончание действия';
COMMENT ON COLUMN bookings.status IS 'Статус';