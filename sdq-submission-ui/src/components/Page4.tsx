

function Page4() {
 
  return (
    <>
      <h2>Page 4</h2>
      <p>Please select Goal type and describe the goal below. When you are done press submit</p>
     
  <form action="" method="post">
    <label for="goaltype">Select Goal type </label> 
    <select id="goaltype" name="goaltype">
      <option
        value="EMOTIONAL">Emotional</option>
      <option
        value="BEHAVIOURAL">Behavioural</option>
      <option
        value="RELATIONAL">Relational</option>
      <option 
        value="REGULATORY_CAPACITY">Regulatory capacity</option>
      <option
        value="TRAUMA_RECOVERY">Trauma recovery</option>
      <option
        value="SELF_ESTEEM_CONFIDENCE">Self esteem confidence</option>
      <option
        value="UNKNOWN">Unknown</option>
    </select>
    <br />
    <br />
    
    <textarea name="goaldescribe" rows="5" cols="50" ></textarea>

    <br />
    <br />

    
  </form>



     
    </>
  );
}

export default Page4;