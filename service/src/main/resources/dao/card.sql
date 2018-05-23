--STATEMENT createCard
INSERT INTO card (
  card_name,
  card_number,
  card_image,
  card_image_mime_type,
  card_type
) VALUES(
  :card_name,
  :card_number,
  :card_image,
  :card_image_mime_type,
  :card_type
);

--STATEMENT readCard
SELECT * FROM card WHERE id = :id;

--STATEMENT updateCard
# TODO: write update function

--STATEMENT deleteCard
DELETE FROM card WHERE id = :id;
