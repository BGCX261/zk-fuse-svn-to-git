Select O.*, A.name as accname, P.name as prodname
  From APP.BANKACCOUNT AS A, APP.PRODUCT AS P,
       (Select H.id as oid, accid, prodid,
               serialno, date, remark, stage, planqty, qty, truckno, produom, seq
          From APP.PO H, APP.POL L
         Where H.id=L.headerid AND h.id in (${IDs})
       ) AS O
 Where A.id=O.accid AND P.id=O.prodid
 Order by date, serialno, seq
