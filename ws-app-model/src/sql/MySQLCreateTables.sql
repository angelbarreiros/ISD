-- ----------------------------------------------------------------------------
-- Model
-- -----------------------------------------------------------------------------
DROP TABLE Reply;
DROP TABLE Event;


CREATE TABLE Event (
    eventId BIGINT NOT NULL AUTO_INCREMENT,
    name VARCHAR(255) COLLATE latin1_bin NOT NULL,
    description VARCHAR(255) COLLATE latin1_bin,
    registerDate DATETIME NOT NULL,
    initCelebrationDate DATETIME NOT NULL,
    participantYes SMALLINT,
    participantNo SMALLINT,
    duration SMALLINT NOT NULL,
    cancelled BIT NOT NULL,
    CONSTRAINT EventPK PRIMARY KEY(eventId),
    CONSTRAINT validDuration CHECK ( 0<= duration )
)ENGINE = InnoDB;

CREATE TABLE Reply (
    replyId BIGINT NOT NULL AUTO_INCREMENT,
    eventId BIGINT NOT NULL,
    email VARCHAR(255) COLLATE latin1_bin NOT NULL,
    answered BIT NOT NULL,
    answerDate DATETIME NOT NULL,
    CONSTRAINT ReplyPK PRIMARY KEY (replyId),
    CONSTRAINT ReplyEventIdFK FOREIGN KEY(eventId)
    REFERENCES Event(eventId) ON DELETE CASCADE
)ENGINE = InnoDB;
