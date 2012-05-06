CREATE TABLE person (
  id INTEGER PRIMARY KEY,
  name VARCHAR(255) NOT NULL,
  mother INTEGER,
  father INTEGER,
  sex TEXT NOT NULL,
  dob TEXT NOT NULL,
  spouse INTEGER,
  height INTEGER NOT NULL,
  weight DOUBLE NOT NULL,
  hair_color CHAR(2) NOT NULL,
  eye_color CHAR(2) NOT NULL,
  hair_color_alt CHAR(2),

  FOREIGN KEY (mother) REFERENCES person (id) ON DELETE SET NULL ON UPDATE CASCADE,
  FOREIGN KEY (father) REFERENCES person (id) ON DELETE SET NULL ON UPDATE CASCADE,
  FOREIGN KEY (spouse) REFERENCES person (id) ON DELETE SET NULL ON UPDATE CASCADE
);

CREATE SEQUENCE person_id_seq;

ALTER TABLE person ALTER COLUMN id SET DEFAULT nextval('person_id_seq');

CREATE TABLE friendmap (
  person_id1 INTEGER NOT NULL,
  person_id2 INTEGER NOT NULL,

  FOREIGN KEY (person_id1) REFERENCES person (id) ON DELETE CASCADE ON UPDATE CASCADE,
  FOREIGN KEY (person_id2) REFERENCES person (id) ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE INDEX friendmap_id1 ON friendmap (person_id1);
CREATE INDEX friendmap_id2 ON friendmap (person_id2);
